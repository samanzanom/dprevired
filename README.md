# Desafío PreviRed

Este proyecto ha sido desarrollado como parte de una prueba de conocimientos en Java, enfocado en la creación de un nuevo módulo para el portal privado de PreviRed. 
El módulo permite a los clientes en empresas registrar la información de las empresas con sus trabajadores activos asociados a través de servicios REST, 
complementado con un FRONTEND interactivo para una gestión fácil y eficiente.

## Características

- **Registro de Empresas:** Permite a los usuarios registrar nuevas empresas, incluyendo datos como el nombre de la empresa, dirección, y otros detalles relevantes.
- **Gestión de Trabajadores:** Facilita el registro y manejo de trabajadores activos asociados a cada empresa, permitiendo agregar, editar o eliminar información de trabajadores.
- **Interfaz Intuitiva:** Un FRONTEND desarrollado con tecnologías modernas para ofrecer una experiencia de usuario fluida y accesible.
- **API RESTful:** Back-end implementado con Spring Boot, ofreciendo una API RESTful para interactuar con la aplicación de manera programática.

## Tecnologías Utilizadas

- **Back-end:** Spring Boot para la creación de servicios REST.
- **Front-end:** Angular para el desarrollo del lado del cliente.
- **Base de Datos:** PostgreSQL para el almacenamiento persistente de datos.
- **Seguridad:** Spring Security para la autenticación y autorización, asegurando el acceso a los datos.


## Empezando

Estas instrucciones te permitirán obtener una copia del proyecto en funcionamiento en tu máquina local para propósitos de desarrollo y pruebas.

### Prerrequisitos

Para instalar y ejecutar este proyecto localmente, necesitarás las siguientes herramientas y versiones:

- Git
- JDK 17
- Node.js v18.18
- npm v9

Asegúrate de tener instaladas estas versiones específicas para evitar problemas de incompatibilidad. Puedes verificar tu versión actual de cada herramienta con los siguientes comandos en tu terminal:

- Para JDK:

  ```shell
  java -version
  ```
  
- Para Node.js:

  ```shell
  node -v
  ``` 
  
- Para npm:

  ```shell
  npm -v
  ``` 
  

### Configuración de la Base de Datos

Antes de ejecutar las aplicaciones localmente, es necesario configurar la base de datos. Sigue los pasos a continuación para ejecutar los scripts necesarios que crearán la base de datos, el usuario, los permisos de usuario, y las tablas necesarias.

Asegúrate de tener instalado PostgreSQL en tu máquina local y de que esté corriendo.

Abre una terminal o consola de comandos.

	
1. Ejecuta el script `init-db.sql` utilizando el comando psql:
   ```shell
   psql -U nombre_de_usuario -d nombre_de_base_de_datos -a -f script/init-db.sql
   ```


### Ejecución Local de la Aplicación Spring Boot

1. Clonar el repositorio

    ```shell
	git clone https://github.com/samanzanom/dprevired.git
	```
	 
2. Navegar al directorio del proyecto

	```shell
    cd dprevired
	```

3. Navegar al directorio de la aplicación Spring Boot

	```shell
	cd fuentes/backend
	```
	
4. Ejecutar la aplicación

	```shell
    ./mvnw spring-boot:run
	```
	
La aplicación Spring Boot ahora estará corriendo y accesible en `http://localhost:8080`.
	
### Documentación de la API con Swagger

La aplicación Spring Boot está configurada con Swagger para ofrecer una documentación interactiva de la API. Puedes acceder a la interfaz de usuario de Swagger para explorar los endpoints disponibles y probarlos directamente desde tu navegador una vez que la aplicación esté corriendo.

Para acceder a la documentación de Swagger, navega a:

	```shell
	http://localhost:8080/swagger-ui.html
	```
	
	
Esto te permitirá ver todos los endpoints disponibles, sus especificaciones y probar las distintas operaciones de la API de forma interactiva.


### Ejecución Local de la Aplicación Angular

1. Navegar al directorio de la aplicación Angular

	```shell
    cd ../frontend
	```
	
2. Instalar las dependencias

	```shell
    npm install
	```
	
3. Ejecutar la aplicación

	```shell
	ng serve
	```
	

La aplicación Angular ahora estará corriendo y accesible en `http://localhost:4200`.

## Construido con

- [Spring Boot](https://spring.io/projects/spring-boot) - El framework utilizado para la aplicación del servidor
- [Angular](https://angular.io/) - El framework utilizado para la aplicación del cliente


## Autor

- **Sebastian Manzano** - *Analista Programador Senior* - [samanzanom](https://github.com/samanzanom)