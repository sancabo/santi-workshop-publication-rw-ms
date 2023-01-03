# Publication Service
A Single database read-write service. Exposes a CRUD api on an example resource called Publication.
A Publication is meant to represent a text user post on a social media.

Also exposes an API for mass data population. 

- POST /publicacion/populator?intensisty - Request creation of an object that continuously saves random data.
  [intensisty] increases number of threads.
- 
- DELETE /publicacion/populator - Request shut down of data population.
- 
- GET /publicacion/populator - You can use this method to see if the populator has stopped or started.

You can use it to generate tens of millions of Publications and test the scalability of the system.

This service is expected to hit a scalability limit. This is intended.

You can check my repo <> to se a version that uses CQRS to improve searches. 
