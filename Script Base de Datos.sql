CREATE DATABASE IF NOT EXISTS VicoDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE VicoDB;

CREATE TABLE IF NOT EXISTS Roles (
    RolID INT AUTO_INCREMENT PRIMARY KEY,
    NombreRol VARCHAR(50) NOT NULL UNIQUE,
    Descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS Usuarios (
    UsuarioID INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Correo VARCHAR(100) NOT NULL UNIQUE,
    Telefono VARCHAR(20),
    ContrasenaHash VARCHAR(255) NOT NULL,
    RolID INT NOT NULL,
    FechaCreacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (RolID) REFERENCES Roles(RolID)
);

CREATE TABLE IF NOT EXISTS Categorias (
    CategoriaID INT AUTO_INCREMENT PRIMARY KEY,
    NombreCategoria VARCHAR(50) NOT NULL UNIQUE,
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
    EstaActivo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (CategoriaID) REFERENCES Categorias(CategoriaID)
);

CREATE TABLE IF NOT EXISTS Ventas (
    VentaID INT AUTO_INCREMENT PRIMARY KEY,
    IDCliente INT,
    IDEmpleado INT,
    TipoVenta VARCHAR(50) DEFAULT 'PuntoDeVenta',
    Estado VARCHAR(50) DEFAULT 'Completada',
    MontoTotal DECIMAL(10,2) NOT NULL,
    MontoEntregado DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    Cambio DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    Moneda VARCHAR(10) NOT NULL DEFAULT 'MXN',
    MetodoPago VARCHAR(30) NOT NULL DEFAULT 'Efectivo',
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

CREATE TABLE IF NOT EXISTS CortesCaja (
    CorteID INT AUTO_INCREMENT PRIMARY KEY,
    FechaCorte DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    IDUsuario INT NOT NULL,
    TotalVentasMXN DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    TotalVentasUSD DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    EfectivoContadoMXN DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    EfectivoContadoUSD DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    DiferenciaMXN DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    DiferenciaUSD DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    TotalTransacciones INT NOT NULL DEFAULT 0,
    Observaciones VARCHAR(255),
    FOREIGN KEY (IDUsuario) REFERENCES Usuarios(UsuarioID)
);

CREATE TABLE IF NOT EXISTS Notificaciones (
    NotificacionID INT AUTO_INCREMENT PRIMARY KEY,
    ProductoID INT,
    Tipo VARCHAR(40) NOT NULL DEFAULT 'StockBajo',
    Mensaje VARCHAR(255) NOT NULL,
    Leida BOOLEAN NOT NULL DEFAULT FALSE,
    FechaCreacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ProductoID) REFERENCES Productos(ProductoID)
);

CREATE TABLE IF NOT EXISTS ConfiguracionTienda (
    ConfigID INT AUTO_INCREMENT PRIMARY KEY,
    NombreTienda VARCHAR(100) NOT NULL,
    Direccion VARCHAR(255),
    Telefono VARCHAR(20),
    Horario VARCHAR(100)
);

-- Datos semilla iniciales (idempotentes)
INSERT IGNORE INTO Roles (RolID, NombreRol, Descripcion) VALUES
    (1, 'Administrador', 'Acceso total al sistema y configuracion'),
    (2, 'Empleado', 'Acceso a punto de venta y consulta de pedidos'),
    (3, 'Cliente', 'Acceso a tienda en linea, carrito y perfil');

INSERT IGNORE INTO Categorias (CategoriaID, NombreCategoria, Descripcion) VALUES
    (1, 'Refrescos', 'Bebidas carbonatadas y refrescos embotellados'),
    (2, 'Jugos', 'Jugos naturales y bebidas de frutas'),
    (3, 'Snacks', 'Botanas, galletas y alimentos empacados'),
    (4, 'Aguas', 'Aguas purificadas y frescas');

INSERT IGNORE INTO Usuarios (UsuarioID, Nombre, Correo, Telefono, ContrasenaHash, RolID) VALUES
    (1, 'Administrador Vico', 'admin@vico.com', '6641000001', 'admin123', 1),
    (2, 'Empleado Mostrador', 'empleado@vico.com', '6641000002', 'empleado123', 2),
    (3, 'Cliente Demo', 'cliente@vico.com', '6641000003', 'cliente123', 3);

INSERT IGNORE INTO Productos (ProductoID, Nombre, Descripcion, Precio, Inventario, URLImagen, CategoriaID, EstaActivo) VALUES
    (1, 'Coca Cola 600ml', 'Refresco de cola 600ml bien frio', 22.00, 35, 'Images/cola.jpg', 1, TRUE),
    (2, 'Agua Natural 1L', 'Agua purificada embotellada 1 litro', 15.00, 40, 'Images/agua.jpg', 4, TRUE),
    (3, 'Jugo de Naranja 500ml', 'Jugo natural de naranja recien exprimido', 28.00, 20, 'Images/jugo_naranja.jpg', 2, TRUE),
    (4, 'Limonada Mineral 500ml', 'Bebida refrescante de limon con agua mineral', 25.00, 18, 'Images/limon.jpg', 2, TRUE),
    (5, 'Papas Clasicas 45g', 'Botana de papas fritas con sal', 18.00, 25, 'Images/papas.jpg', 3, TRUE),
    (6, 'Galletas Surtidas', 'Paquete de galletas dulces', 20.00, 4, 'Images/galletas.jpg', 3, TRUE);

INSERT IGNORE INTO ConfiguracionTienda (ConfigID, NombreTienda, Direccion, Telefono, Horario) VALUES
    (1, 'Refresqueria Vico', 'Av. Principal 123, Tijuana B.C.', '664-555-1234', '9:00 - 18:00');
