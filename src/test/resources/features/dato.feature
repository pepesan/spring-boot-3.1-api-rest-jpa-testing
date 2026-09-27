# language: es
Característica: API de dato
  Como cliente de la API
  Quiero crear y consultar datos
  Para verificar el comportamiento del endpoint /api/v1/dato

  Antecedentes:
    Dado que no hay ningún dato guardado

  Escenario: Crear un dato y consultarlo por id
    Cuando creo un dato con la cadena "hola cucumber"
    Entonces la respuesta tiene código 200
    Y el dato creado tiene la cadena "hola cucumber"
    Cuando consulto el dato por su id
    Entonces la respuesta tiene código 200
    Y el dato consultado tiene la cadena "hola cucumber"

  Escenario: Consultar un dato que no existe devuelve 404
    Cuando consulto el dato con id 999
    Entonces la respuesta tiene código 404
