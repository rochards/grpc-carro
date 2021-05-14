# Testando aplicação com gRPC, Micronaut e Kotlin

Exemplo de criação de testes integrados com gRPC, Micronaut e Kotlin. A aplicação se dá em cenário de persistir uma 
entidade Carro com apenas dois campos: modelo e placa. A restrição é não salvar dois carros com placas iguais.

### Container PostgreSQL no Docker
- Banco de dados baseado na imagem do Postgres: ``docker run -p 5432:5432 --name grpc-carro-db -e POSTGRES_PASSWORD=mysecret -e POSTGRES_DB=carro_db postgres``;
- Caso queira inspecionar o banco: ``winpty docker exec -it grpc-carro-db  psql -U postgres -W carro_db``;
    - Mostrar tabelas: ``\dt``
  
### O que foi utilizado para a implementação deste exemplo
- JDK 11;
- Intellij IDEA;
- Kotlin 1.5.0;
- Micronaut 2.5.3;
- gRPC.
