# FileSharingP2P
Primer Parcial Programación Distribuida
Integrantes
Pedro David Acevedo
Eduardo Angulo
Carlos Conrado
Andres Ramirez

_____________Instruciones________________

Requerido hacer Clean-And-Build en cada proyecto
Servidor Brigde

	Correr WebServiceLoadBalancer
	Correr BrigdeTCPMulticast

Servidor Multicast Receptor

	Correr MulticastReceptorApp
	Correr WebServiceRESTMulticast

Client

	Correr ClientApp
	
Web Service Client
	
	WebServiceClient
	
Antes de conectar editar el archivo. ..\MulticastReceptorApp\src\com\distri\resources\config\IP con la IP de la maquina que tenga el WebServiceLoadBalancer
	
-Carpeta de almacenamiento de archivos MulticastReceptorApp: 
...\WebServiceRESTMulticast\src\main\java\com\distri\webservicerestmulticast\resources

-Carpeta de almacenamiento del cliente webservice:
...\WebServiceClient\resources

________________Alcance___________________

+ Todo implementado, hasta la modificación.
Debido a la implementación dentro de el envio por multicast, los archvios grandes pierden paquetes por lo que corrrompen los archivos.



	