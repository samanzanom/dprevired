# Paso 1: Utiliza una imagen de Node.js como base para construir tu aplicación
FROM node:18 as build

# Establece el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el archivo 'package.json' y 'package-lock.json' (o 'npm-shrinkwrap.json')
COPY package*.json ./

# Instala las dependencias del proyecto
RUN npm install

# Copia el resto de los archivos de tu proyecto al directorio de trabajo en el contenedor
COPY . .

# Construye la aplicación para producción
RUN npm run build

# Paso 2: Usa una imagen de nginx para servir la aplicación
FROM nginx:alpine

# Copia tu archivo de configuración de Nginx personalizado al contenedor
COPY ./nginx/default.conf /etc/nginx/conf.d/default.conf

# Copia los archivos de construcción desde el paso anterior al directorio de servicio de nginx
COPY --from=build /app/dist/* /usr/share/nginx/html/

# Expone el puerto 80 para acceder a la aplicación
EXPOSE 80

# Cuando el contenedor inicia, ejecuta nginx y mantiene el proceso en primer plano
CMD ["nginx", "-g", "daemon off;"]
