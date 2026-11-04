-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 04-11-2026 a las 21:16:53
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `glpi_local`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tecnicos`
--

CREATE TABLE `tecnicos` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) DEFAULT NULL,
  `usuario` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tecnicos`
--

INSERT INTO `tecnicos` (`id`, `nombre`, `usuario`, `password`) VALUES
(1, 'Julian Acuña', 'admin', '1234'),
(2, 'Juan Acuna', 'Tecnico1', '12345'),
(3, 'Julian Cipagauta', 'admin1', '12345');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tickets`
--

CREATE TABLE `tickets` (
  `id` int(11) NOT NULL,
  `cliente` varchar(100) NOT NULL,
  `equipo` varchar(100) NOT NULL,
  `falla` text NOT NULL,
  `estado` varchar(20) DEFAULT 'abierto',
  `marca_equipo` varchar(100) DEFAULT '',
  `clasificacion_equipo` varchar(100) DEFAULT '',
  `repuesto` varchar(255) DEFAULT '',
  `notas` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tickets`
--

INSERT INTO `tickets` (`id`, `cliente`, `equipo`, `falla`, `estado`, `marca_equipo`, `clasificacion_equipo`, `repuesto`, `notas`) VALUES
(3, 'Andres Garcia', 'PC de escritorio', 'Pantalla azul al encender', 'abierto', '', '', '', NULL),
(4, 'Laura Gomez', 'Mouse inalambrico', 'El clic izquierdo no responde', 'abierto', '', '', '', NULL),
(5, 'Laura Gomez', 'Mouse inalambrico', 'El clic izquierdo no responde', 'cerrado', '', '', '', NULL),
(6, 'test user name', 'Pendiente de clasificación', 'this is a text message', 'cerrado', '', '', '', NULL),
(7, 'test user name', 'Impresora', 'this is a text message', 'abierto', '', '', '', NULL),
(8, 'Julian', 'Laptop', 'Teclado Falla', 'en_proceso', 'Asus', 'PC de escritorio', 'ssd 512gb', 'Reemplazo para El 3 de octubre'),
(9, 'Juan', 'impresora', 'no sale el papel', 'cerrado', '', '', '', NULL);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `tecnicos`
--
ALTER TABLE `tecnicos`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `tickets`
--
ALTER TABLE `tickets`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `tecnicos`
--
ALTER TABLE `tecnicos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tickets`
--
ALTER TABLE `tickets`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
