# language: es
Característica: API de bienvenida
  Como cliente de la API
  Quiero acceder al endpoint raíz
  Para verificar el comportamiento del endpoint /

  Escenario: Obtener el saludo de bienvenida
    Cuando accedo a la raíz de la API
    Entonces la respuesta de la raíz tiene código 200
    Y el cuerpo de la respuesta es "Hola Mundo"

  Escenario: Enviar un POST a la raíz no está permitido
    Cuando envío un POST a la raíz de la API
    Entonces la respuesta de la raíz tiene código 405
