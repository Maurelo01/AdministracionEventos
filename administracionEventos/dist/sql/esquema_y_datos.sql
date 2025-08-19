-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: localhost    Database: eventos_db
-- ------------------------------------------------------
-- Server version	8.0.43-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `actividad`
--

DROP TABLE IF EXISTS `actividad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `actividad` (
  `codigo` varchar(20) NOT NULL,
  `evento_codigo` varchar(20) NOT NULL,
  `tipo` enum('CHARLA','TALLER','DEBATE','OTRA') NOT NULL,
  `titulo` varchar(200) NOT NULL,
  `encargado_correo` varchar(100) NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `cupo_maximo` int NOT NULL,
  PRIMARY KEY (`codigo`),
  KEY `evento_codigo` (`evento_codigo`),
  KEY `encargado_correo` (`encargado_correo`),
  CONSTRAINT `actividad_ibfk_1` FOREIGN KEY (`evento_codigo`) REFERENCES `evento` (`codigo`) ON DELETE CASCADE,
  CONSTRAINT `actividad_ibfk_2` FOREIGN KEY (`encargado_correo`) REFERENCES `participante` (`correo`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actividad`
--

LOCK TABLES `actividad` WRITE;
/*!40000 ALTER TABLE `actividad` DISABLE KEYS */;
INSERT INTO `actividad` VALUES ('ACT-001','EVT-ACT-001','CHARLA','Charla de Tecnología Antigua','impa@hyrule.com','09:00:00','10:30:00',30),('act-01','EVT-001','CHARLA','sadasda','asistente@hyrule.com','10:30:00','21:30:00',3);
/*!40000 ALTER TABLE `actividad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asistencia`
--

DROP TABLE IF EXISTS `asistencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asistencia` (
  `id` int NOT NULL AUTO_INCREMENT,
  `participante_correo` varchar(100) NOT NULL,
  `actividad_codigo` varchar(20) NOT NULL,
  `fecha_registro` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `participante_correo` (`participante_correo`,`actividad_codigo`),
  KEY `actividad_codigo` (`actividad_codigo`),
  CONSTRAINT `asistencia_ibfk_1` FOREIGN KEY (`participante_correo`) REFERENCES `participante` (`correo`) ON DELETE CASCADE,
  CONSTRAINT `asistencia_ibfk_2` FOREIGN KEY (`actividad_codigo`) REFERENCES `actividad` (`codigo`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asistencia`
--

LOCK TABLES `asistencia` WRITE;
/*!40000 ALTER TABLE `asistencia` DISABLE KEYS */;
/*!40000 ALTER TABLE `asistencia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `certificado`
--

DROP TABLE IF EXISTS `certificado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `certificado` (
  `id` int NOT NULL AUTO_INCREMENT,
  `participante_correo` varchar(100) NOT NULL,
  `evento_codigo` varchar(20) NOT NULL,
  `fecha_emision` datetime NOT NULL,
  `ruta_archivo` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_certificado` (`participante_correo`,`evento_codigo`),
  KEY `fk_cert_evento` (`evento_codigo`),
  CONSTRAINT `fk_cert_evento` FOREIGN KEY (`evento_codigo`) REFERENCES `evento` (`codigo`) ON DELETE CASCADE,
  CONSTRAINT `fk_cert_participante` FOREIGN KEY (`participante_correo`) REFERENCES `participante` (`correo`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `certificado`
--

LOCK TABLES `certificado` WRITE;
/*!40000 ALTER TABLE `certificado` DISABLE KEYS */;
/*!40000 ALTER TABLE `certificado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evento`
--

DROP TABLE IF EXISTS `evento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evento` (
  `codigo` varchar(20) NOT NULL,
  `fecha` date NOT NULL,
  `tipo` enum('CHARLA','CONGRESO','TALLER','DEBATE') NOT NULL,
  `titulo` varchar(200) NOT NULL,
  `ubicacion` varchar(150) NOT NULL,
  `cupo_maximo` int NOT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evento`
--

LOCK TABLES `evento` WRITE;
/*!40000 ALTER TABLE `evento` DISABLE KEYS */;
INSERT INTO `evento` VALUES ('EVT-001','2025-08-25','CHARLA','Tecnología Sheikah','Auditorio',3),('EVT-ACT-001','2025-09-01','TALLER','Taller Maestría Sheikah','Salón 1',100),('EVT-DEMO','2025-08-23','TALLER','Evento DEMO','Auditorio Principal',3),('EVT-TEST','2025-08-15','CHARLA','Evento Test','Auditorio',2);
/*!40000 ALTER TABLE `evento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inscripcion`
--

DROP TABLE IF EXISTS `inscripcion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inscripcion` (
  `id` int NOT NULL AUTO_INCREMENT,
  `participante_correo` varchar(100) NOT NULL,
  `evento_codigo` varchar(20) NOT NULL,
  `tipo` enum('ASISTENTE','CONFERENCISTA','TALLERISTA','OTRO') NOT NULL,
  `validada` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_inscripcion` (`participante_correo`,`evento_codigo`),
  KEY `fk_insc_evento` (`evento_codigo`),
  CONSTRAINT `fk_insc_evento` FOREIGN KEY (`evento_codigo`) REFERENCES `evento` (`codigo`) ON DELETE CASCADE,
  CONSTRAINT `fk_insc_participante` FOREIGN KEY (`participante_correo`) REFERENCES `participante` (`correo`) ON DELETE CASCADE,
  CONSTRAINT `inscripcion_ibfk_1` FOREIGN KEY (`participante_correo`) REFERENCES `participante` (`correo`) ON DELETE CASCADE,
  CONSTRAINT `inscripcion_ibfk_2` FOREIGN KEY (`evento_codigo`) REFERENCES `evento` (`codigo`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inscripcion`
--

LOCK TABLES `inscripcion` WRITE;
/*!40000 ALTER TABLE `inscripcion` DISABLE KEYS */;
INSERT INTO `inscripcion` VALUES (1,'test@hyrule.com','EVT-TEST','ASISTENTE',1),(2,'zelda@hyrule.edu','EVT-001','ASISTENTE',1),(3,'impa@hyrule.com','EVT-ACT-001','CONFERENCISTA',1),(4,'asistente@hyrule.com','EVT-ACT-001','ASISTENTE',0),(5,'encargado.demo@pruebas.com','EVT-DEMO','CONFERENCISTA',1),(6,'alumno.demo@pruebas.com','EVT-DEMO','ASISTENTE',1),(7,'maure@sda.com','EVT-001','ASISTENTE',1),(10,'asistente@hyrule.com','EVT-001','CONFERENCISTA',1);
/*!40000 ALTER TABLE `inscripcion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago`
--

DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `id` int NOT NULL AUTO_INCREMENT,
  `participante_correo` varchar(100) NOT NULL,
  `evento_codigo` varchar(20) NOT NULL,
  `metodo` enum('EFECTIVO','TRANSFERENCIA','TARJETA') NOT NULL,
  `monto` decimal(10,2) NOT NULL,
  `fecha_pago` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_pago_participante` (`participante_correo`),
  KEY `fk_pago_evento` (`evento_codigo`),
  CONSTRAINT `fk_pago_evento` FOREIGN KEY (`evento_codigo`) REFERENCES `evento` (`codigo`) ON DELETE CASCADE,
  CONSTRAINT `fk_pago_participante` FOREIGN KEY (`participante_correo`) REFERENCES `participante` (`correo`) ON DELETE CASCADE,
  CONSTRAINT `pago_ibfk_1` FOREIGN KEY (`participante_correo`) REFERENCES `participante` (`correo`) ON DELETE CASCADE,
  CONSTRAINT `pago_ibfk_2` FOREIGN KEY (`evento_codigo`) REFERENCES `evento` (`codigo`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago`
--

LOCK TABLES `pago` WRITE;
/*!40000 ALTER TABLE `pago` DISABLE KEYS */;
INSERT INTO `pago` VALUES (1,'test@hyrule.com','EVT-TEST','EFECTIVO',50.00,'2025-08-15 11:59:47'),(2,'zelda@hyrule.edu','EVT-001','EFECTIVO',50.00,'2025-08-15 18:26:17'),(3,'zelda@hyrule.edu','EVT-001','EFECTIVO',50.00,'2025-08-15 23:17:21'),(4,'impa@hyrule.com','EVT-ACT-001','EFECTIVO',50.00,'2025-08-16 14:12:05'),(5,'impa@hyrule.com','EVT-ACT-001','EFECTIVO',50.00,'2025-08-16 14:12:05'),(6,'impa@hyrule.com','EVT-ACT-001','EFECTIVO',50.00,'2025-08-16 14:13:06'),(7,'impa@hyrule.com','EVT-ACT-001','EFECTIVO',50.00,'2025-08-16 14:14:10'),(8,'impa@hyrule.com','EVT-ACT-001','EFECTIVO',50.00,'2025-08-16 14:18:37'),(9,'impa@hyrule.com','EVT-ACT-001','EFECTIVO',50.00,'2025-08-16 14:22:26'),(10,'impa@hyrule.com','EVT-ACT-001','EFECTIVO',50.00,'2025-08-16 14:23:16'),(11,'impa@hyrule.com','EVT-ACT-001','EFECTIVO',50.00,'2025-08-16 14:24:58'),(12,'alumno.demo@pruebas.com','EVT-DEMO','EFECTIVO',50.00,'2025-08-16 17:17:24'),(13,'alumno.demo@pruebas.com','EVT-DEMO','EFECTIVO',50.00,'2025-08-16 17:19:01'),(14,'alumno.demo@pruebas.com','EVT-DEMO','EFECTIVO',50.00,'2025-08-16 20:15:11'),(15,'encargado.demo@pruebas.com','EVT-DEMO','EFECTIVO',10.00,'2025-08-16 20:24:55'),(16,'maure@sda.com','EVT-001','TRANSFERENCIA',50.00,'2025-08-19 11:35:03'),(18,'asistente@hyrule.com','EVT-001','TRANSFERENCIA',49.00,'2025-08-19 11:38:36');
/*!40000 ALTER TABLE `pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `participante`
--

DROP TABLE IF EXISTS `participante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `participante` (
  `correo` varchar(100) NOT NULL,
  `nombre_completo` varchar(45) NOT NULL,
  `tipo` enum('ESTUDIANTE','PROFESIONAL','INVITADO') NOT NULL,
  `institucion` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`correo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `participante`
--

LOCK TABLES `participante` WRITE;
/*!40000 ALTER TABLE `participante` DISABLE KEYS */;
INSERT INTO `participante` VALUES ('alumno.demo@pruebas.com','Alumno Demo','ESTUDIANTE','Instituto Demo'),('asistente@hyrule.com','Asistente Demo','ESTUDIANTE','Academia Real 2'),('encargado.demo@pruebas.com','Encargado Demo','PROFESIONAL','Comité Demo'),('impa@hyrule.com','Impa Sheikah','PROFESIONAL','Clan Sheikah'),('maure@sda.com','asdasd','PROFESIONAL','sdadsa'),('test@hyrule.com','Tester Hyrule','ESTUDIANTE','Academia'),('zelda@hyrule.edu','Zelda Hyrule','ESTUDIANTE','Universidad de Hyrule');
/*!40000 ALTER TABLE `participante` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'eventos_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-19 14:10:26
