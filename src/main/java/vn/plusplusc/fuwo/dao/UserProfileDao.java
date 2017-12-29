package vn.plusplusc.fuwo.dao;

import java.util.List;

import vn.plusplusc.fuwo.model.UserProfile;


public interface UserProfileDao {

	List<UserProfile> findAll();
	
	UserProfile findByType(String type);
	
	UserProfile findById(int id);
}
