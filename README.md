# ORIGINAL REQUIREMENTS

- The system should allow reservation of a car of a given type at a desired date and time for a given
number of days - [test link](https://github.com/mzakidalski/statestreet_homework/blob/b808c8e535af77acee0565816775adddb40a38d0/src/test/java/com/statestreet/carreservation/controller/reservartion/ReservationControllerIntegrationTest.java#L42-L41).
- There are 3 types of cars (Sedan, SUV and van) - [test link](https://github.com/mzakidalski/statestreet_homework/blob/9b977ebeb745143f5b11cba505e567db66ba6be8/src/test/java/com/statestreet/carreservation/controller/car/CarControllerIntegrationTest.java#L154-L153).
- The number of cars of each type is limited - [test link](https://github.com/mzakidalski/statestreet_homework/blob/b808c8e535af77acee0565816775adddb40a38d0/src/test/java/com/statestreet/carreservation/controller/reservartion/ReservationControllerIntegrationTest.java#L79-L78).

# MADE ASSUMPTIONS
- reservation request will be done specifying car type, start date, end date and user id
- no booking of specific car, all cars of given type are equivalent

# LIMITATIONS
- error handling
- UI
- dockerization, security etc.

# ALTERNATIVE APPROACH
- store counters of each type of car instead of storing whole car records

# AI USAGE / PROMPTING
- generating stubs of tests / boilerplate code
- errors troubleshooting
- asking for specific features of Spring (e.g. JPQL)


