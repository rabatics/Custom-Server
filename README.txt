This is a Forum application running over the TinyHttpd2 server.
Before using the application, please update DBConnection.getConnection() with your mysql username and password and database name.
The database tables will be re-initialized on every new instance of the server.
To run the application, execute 'ant' in the project folder.
Then open a browser and go to the URL: localhost:4000/files/index.html
The default user for the Forum application is username=user ,  password=1111
You can also register to make a new account. 
After logging in , Type in the text box and submit to add a new post.

Currently the app doesnt support special characters in the text.

The server handles incoming request sockets by sending the request to the appropriate controller. 
All the controllers implement the Controller interface.
