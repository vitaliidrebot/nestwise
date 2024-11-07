# Use the official Node.js image as the base image
FROM node:23.1-alpine

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install dependencies
RUN npm install --verbose

# Copy the rest of the application code to the working directory
COPY . .

## Build the React application
#RUN npm run build
#
## Install serve to serve the build files
#RUN npm install -g serve

# Expose the port the app runs on
EXPOSE 3000

# Command to run the application
#CMD ["serve", "-s", "build"]

CMD ["npm", "start"]