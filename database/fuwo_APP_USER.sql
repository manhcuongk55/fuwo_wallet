-- MySQL dump 10.13  Distrib 5.7.20, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: fuwo
-- ------------------------------------------------------
-- Server version	5.7.20-0ubuntu0.16.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `APP_USER`
--

DROP TABLE IF EXISTS `APP_USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APP_USER` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sso_id` varchar(30) NOT NULL,
  `password` varchar(100) NOT NULL,
  `first_name` varchar(30) NOT NULL,
  `last_name` varchar(30) NOT NULL,
  `email` varchar(30) NOT NULL,
  `phone` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sso_id` (`sso_id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `APP_USER`
--

LOCK TABLES `APP_USER` WRITE;
/*!40000 ALTER TABLE `APP_USER` DISABLE KEYS */;
INSERT INTO `APP_USER` VALUES (52,'dmcksclck55','$2a$10$rfFDS1XQCx50.Waq8fD/cuhaF9mQvNHoiFhshFnpp8GoIOjmnQswW','Dinh','Manh Cuong','dmcksclck55@gmail.com',''),(53,'1dmcksclck55','$2a$10$.1YSIFu/Wdy4JIHG9Canj.FzIVrBmOzX.2rHJ0MgWYRRIkA7s/a5O','Dinh','Manh Cuong','1dmcksclck55@gmail.com',''),(54,'11dmcksclck55','$2a$10$009vhK9wwqX9Cag/bLv23eu.RwYc.bk3Un0aKRVDJ6hruHtVkohVy','','','11dmcksclck55@gmail.com',''),(55,'01674729310','$2a$10$V4MbKpyNjrOa/tg76yiVueXlWppEND2LDbq0FeqMaU/ih/0MXhBT.','','','','01674729310'),(56,'abc','$2a$10$1ssCH8vuh2BxXiHFiSHM2.94zTrcXqnkX6Rti6RUlzXgbwNfI.cqm','','','abc@gmail.com','');
/*!40000 ALTER TABLE `APP_USER` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-01-12 17:51:33
