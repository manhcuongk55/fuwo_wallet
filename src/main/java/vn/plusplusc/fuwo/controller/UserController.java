package vn.plusplusc.fuwo.controller;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import vn.plusplusc.fuwo.dao.HibernateTokenRepositoryImpl;
import vn.plusplusc.fuwo.model.User;
import vn.plusplusc.fuwo.model.UserProfile;
import vn.plusplusc.fuwo.service.UserProfileService;
import vn.plusplusc.fuwo.service.UserService;
import vn.plusplusc.fuwo.model.MyEntry;

@Controller
@SessionAttributes("roles")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	UserProfileService userProfileService;

	@Autowired
	MessageSource messageSource;

	@Autowired
	PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;

	@Autowired
	AuthenticationTrustResolver authenticationTrustResolver;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PersistentTokenRepository persistentTokenRepository;

	@RequestMapping(value = { "/user/registration" }, method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Object register(@RequestBody User user) {
		String ssoId = "";
		if(!"".equals(user.getPhone())){
			ssoId = user.getPhone();
		}else{
			int index = user.getEmail().indexOf('@');
		    ssoId = user.getEmail().substring(0, index);
		}
		if (!userService.isUserSSOUnique(user.getId(), ssoId)) {
			FieldError ssoError = new FieldError("user", "ssoId", messageSource.getMessage("non.unique.ssoId",
					new String[] { user.getSsoId() }, Locale.getDefault()));
			return ssoError;
		}
		user.setSsoId(ssoId);
		PersistentRememberMeToken persistentToken = new PersistentRememberMeToken(user.getSsoId(),
				HibernateTokenRepositoryImpl.generateSeriesData(), HibernateTokenRepositoryImpl.generateTokenData(),
				new Date());
		persistentTokenRepository.createNewToken(persistentToken);
		userService.saveUser(user);
		ArrayList<MyEntry<String, Object>> arr = new ArrayList<MyEntry<String, Object>>();
		MyEntry<String, Object> entry = new MyEntry<String, Object>("user", user);
		arr.add(entry);
		arr.add(new MyEntry<String, Object>("token", persistentToken));
		return arr;
	}

	/**
	 * This method handles login GET requests. If users is already logged-in and
	 * tries to goto login page again, will be redirected to list page.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public String loginPage(HttpServletRequest request, HttpServletResponse response) {
		if (isCurrentAuthenticationAnonymous()) {
			return "login";
		} else {
			return "redirect:/list";
		}
	}

	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	@ResponseBody
	public Object login(@RequestBody User user) {
		String ssoId = "";
		if(!"".equals(user.getPhone())){
			ssoId = user.getPhone();
		}else{
			int index = user.getEmail().indexOf('@');
		    ssoId = user.getEmail().substring(0, index);
		}
		User dbuser = userService.findBySSO(ssoId);
		if (dbuser != null) {
			if (passwordEncoder.matches(user.getPassword(), dbuser.getPassword())) {
				PersistentRememberMeToken persistentToken = new PersistentRememberMeToken(dbuser.getSsoId(),
						HibernateTokenRepositoryImpl.generateSeriesData(),
						HibernateTokenRepositoryImpl.generateTokenData(), new Date());
				persistentTokenRepository.createNewToken(persistentToken);
				ArrayList<MyEntry<String, Object>> arr = new ArrayList<MyEntry<String, Object>>();
				MyEntry<String, Object> entry = new MyEntry<String, Object>("user", dbuser);
				arr.add(entry);
				arr.add(new MyEntry<String, Object>("token", persistentToken));
				return arr;
			}
			return "error";
		}
		return dbuser;

	}

	/**
	 * This method will be called on form submission, handling POST request for
	 * updating user in database. It also validates the user input
	 */
	@RequestMapping(value = { "/edit-user-{ssoId}" }, method = RequestMethod.POST)
	public String updateUser(@Valid User user, @PathVariable String ssoId) {
		userService.updateUser(user);
		return "update";
	}

	/**
	 * This method will delete an user by it's SSOID value.
	 */
	@RequestMapping(value = { "/delete-user-{ssoId}" }, method = RequestMethod.GET)
	public String deleteUser(@PathVariable String ssoId) {
		userService.deleteUserBySSO(ssoId);
		return "delete";
	}

	/**
	 * This method will provide UserProfile list to views
	 */
	@ModelAttribute("roles")
	public List<UserProfile> initializeProfiles() {
		return userProfileService.findAll();
	}

	private boolean isCurrentAuthenticationAnonymous() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authenticationTrustResolver.isRememberMe(authentication);
	}

}