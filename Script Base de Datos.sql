CREATE DATABASE IF NOT EXISTS VicoDB;
USE VicoDB;

CREATE TABLE IF NOT EXISTS Roles (
    RolID INT AUTO_INCREMENT PRIMARY KEY,
    NombreRol VARCHAR(50) NOT NULL,
    Descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Usuarios (
    UsuarioID INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Correo VARCHAR(100) NOT NULL UNIQUE,
    Telefono VARCHAR(20),
    ContrasenaHash VARCHAR(255) NOT NULL,
    RolID INT,
    FechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (RolID) REFERENCES Roles(RolID)
);

CREATE TABLE IF NOT EXISTS Categorias (
    CategoriaID INT AUTO_INCREMENT PRIMARY KEY,
    NombreCategoria VARCHAR(50) NOT NULL,
    Descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Productos (
    ProductoID INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Descripcion TEXT,
    Precio DECIMAL(10,2) NOT NULL,
    Inventario INT NOT NULL DEFAULT 0,
    URLImagen VARCHAR(255),
    CategoriaID INT,
    EstaActivo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (CategoriaID) REFERENCES Categorias(CategoriaID)
);

CREATE TABLE IF NOT EXISTS Ventas (
    VentaID INT AUTO_INCREMENT PRIMARY KEY,
    IDCliente INT,
    IDEmpleado INT,
    TipoVenta VARCHAR(50),
    Estado VARCHAR(50),
    MontoTotal DECIMAL(10,2) NOT NULL,
    FechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (IDCliente) REFERENCES Usuarios(UsuarioID),
    FOREIGN KEY (IDEmpleado) REFERENCES Usuarios(UsuarioID)
);

CREATE TABLE IF NOT EXISTS DetallesVenta (
    DetalleID INT AUTO_INCREMENT PRIMARY KEY,
    VentaID INT NOT NULL,
    ProductoID INT NOT NULL,
    Cantidad INT NOT NULL,
    PrecioVenta DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (VentaID) REFERENCES Ventas(VentaID),
    FOREIGN KEY (ProductoID) REFERENCES Productos(ProductoID)
);

CREATE TABLE IF NOT EXISTS ConfiguracionTienda (
    ConfigID INT AUTO_INCREMENT PRIMARY KEY,
    NombreTienda VARCHAR(100) NOT NULL,
    Direccion VARCHAR(255),
    Telefono VARCHAR(20),
    Horario VARCHAR(100)
);

-- Datos iniciales
INSERT INTO Roles (NombreRol, Descripcion) VALUES ('Administrador', 'Acceso total');
INSERT INTO Roles (NombreRol, Descripcion) VALUES ('Empleado', 'Acceso a ventas');
INSERT INTO ConfiguracionTienda (NombreTienda, Direccion, Telefono, Horario) VALUES ('Refresqueria Vico', 'Direccion', '555-1234', '9:00 - 18:00');
