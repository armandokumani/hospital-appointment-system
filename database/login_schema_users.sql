-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: login_schema
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `name` varchar(50) NOT NULL,
  `surname` varchar(50) NOT NULL,
  `age` int NOT NULL,
  `type` enum('ADMIN','DOCTOR','PATIENT') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2y$10$mDJtD5AoRpPy1mCWC.IyeeFHQMs0RS1B3XP6ZPDUABJ/bPDMMt48O','System','Admin',40,'ADMIN'),(2,'doc1','pass1','John','Doe',45,'DOCTOR'),(3,'doc2','pass2','Jane','Smith',38,'DOCTOR'),(4,'pat1','pass1','Mike','Johnson',30,'PATIENT'),(5,'pat2','pass2','Sarah','Williams',25,'PATIENT'),(6,'panos_','$2a$12$t7yj0EW0hrgq4TGdgllZ3OHvLTGZFcBayPAvz8F.dCgnRtOryi9KG','panos','Geo',20,'PATIENT'),(7,'alexuser','$2a$12$RW75RxKQz9MJJXWj5PGffOgQ2gUqoQsQuwWTHJQ6Rj37DHPMnbPWa','Alex','Spat',21,'DOCTOR'),(8,'thanosuser','$2a$12$YffMWn0X2aDZlJY8l8ASIOEOQ93GnQrAQnFonZPzqMxkCCIrR06Me','Thanos','Kotsou',19,'PATIENT'),(10,'batman','$2a$12$Hem3zBCWDwk8Uxxv/GqAzu78yMsrDb8JfF8cMU3kANAqDox2H4hna','Aggelos','Psaras',24,'PATIENT'),(11,'nbaking23','$2a$12$ONzWo.9s.ejP/3lGXD0jOOINFCQIaZTK.m3sR1RKRCMxs7LbkVKHm','Vaios','Kleisiaris',23,'DOCTOR'),(12,'zywarrior','$2a$12$tEyLwAsHTfmnsi8wp1H4z.aC84yJNycqmYdsdYNiq7IryoKcCBqu.','Παναγιώτης','Ζυγογιάννης',20,'PATIENT'),(13,'chriswest','$2a$12$YG2mmqpQYkrRRcn5dUMo4u.ORbZ1YQiMnf6wzlr7rSo.4h7ljT.ai','chris','west',69,'DOCTOR');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-16  4:49:39
