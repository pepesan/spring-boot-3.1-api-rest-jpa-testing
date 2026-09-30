# language: es
Característica: API de datos en memoria
  Como cliente de la API
  Quiero listar, crear, consultar, modificar y eliminar datos
  Para verificar el comportamiento del endpoint /api/dato

  Antecedentes:
    Dado que el listado de datos está vacío

  Escenario: Listar sin datos devuelve una lista vacía
    Cuando pido el listado de datos
    Entonces la respuesta del listado tiene código 200 y es JSON
    Y el listado devuelto está vacío

  Escenario: Añadir un dato devuelve el dato con su id
    Cuando añado un dato al listado con la cadena "valor"
    Entonces la respuesta del listado tiene código 200 y es JSON
    Y el dato del listado devuelto tiene id 1 y cadena "valor"

  Escenario: Consultar un dato por su id
    Y añado un dato al listado con la cadena "valor"
    Cuando pido el dato del listado con id 1
    Entonces la respuesta del listado tiene código 200 y es JSON
    Y el dato del listado devuelto tiene id 1 y cadena "valor"

  Escenario: Modificar un dato existente
    Y añado un dato al listado con la cadena "valor"
    Cuando modifico el dato del listado con id 1 con la cadena "valor1"
    Entonces la respuesta del listado tiene código 200 y es JSON
    Y el dato del listado devuelto tiene id 1 y cadena "valor1"

  Escenario: Eliminar un dato existente
    Y añado un dato al listado con la cadena "valor"
    Cuando elimino el dato del listado con id 1
    Entonces la respuesta del listado tiene código 200 y es JSON
    Y el dato del listado devuelto tiene id 1 y cadena "valor"

  Escenario: Eliminar un dato inexistente devuelve un dato vacío
    Cuando elimino el dato del listado con id 999
    Entonces la respuesta del listado tiene código 200
    Y el dato del listado devuelto está vacío

  Escenario: Modificar parcialmente un dato existente con un parche
    Y añado un dato al listado con la cadena "valor"
    Cuando aplico un parche al dato del listado con id 1 con el campo "cadena" y valor "valor1"
    Entonces la respuesta del listado tiene código 200 y es JSON
    Y el dato del listado devuelto tiene id 1 y cadena "valor1"

  Escenario: Un parche con un valor no válido devuelve 400 y no modifica el dato
    Y añado un dato al listado con la cadena "valor"
    Cuando aplico un parche al dato del listado con id 1 con el campo "cadena" y valor "abc"
    Entonces la respuesta del listado tiene código 400 y es JSON
    Cuando pido el dato del listado con id 1
    Entonces el dato del listado devuelto tiene id 1 y cadena "valor"

  Escenario: Un parche sobre un dato inexistente devuelve 404
    Cuando aplico un parche al dato del listado con id 999 con el campo "cadena" y valor "valor1"
    Entonces la respuesta del listado tiene código 404 y es JSON

  Escenario: Un parche con un campo desconocido lo ignora y no modifica el dato
    Y añado un dato al listado con la cadena "valor"
    Cuando aplico un parche al dato del listado con id 1 con el campo "inexistente" y valor "x"
    Entonces la respuesta del listado tiene código 200 y es JSON
    Y el dato del listado devuelto tiene id 1 y cadena "valor"

  Escenario: Un parche con Content-Type JSON normal devuelve 415
    Y añado un dato al listado con la cadena "valor"
    Cuando aplico un parche con Content-Type JSON normal al dato del listado con id 1 con el campo "cadena" y valor "valor1"
    Entonces la respuesta del listado tiene código 415
