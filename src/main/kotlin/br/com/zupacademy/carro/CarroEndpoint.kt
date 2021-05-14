package br.com.zupacademy.carro

import io.grpc.Status
import io.grpc.stub.StreamObserver
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class CarroEndpoint(private val carroRepository: CarroRepository) : GrpcCarroServiceGrpc.GrpcCarroServiceImplBase() {

    override fun adiciona(request: CarroRequest, responseObserver: StreamObserver<CarroResponse>) {

        if (carroRepository.existsByPlaca(request.placa)) {
            responseObserver.onError(Status.ALREADY_EXISTS
                .withDescription("carro com placa existente")
                .asRuntimeException())

            return
        }

//        println("placa em branco: ${request.placa.isBlank()}")

        val carro = Carro(request.modelo, request.placa)
        try {
            carroRepository.save(carro)
        } catch (e: ConstraintViolationException) {
            println(e.message)
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("dados de entrada inválidos")
                .asRuntimeException())

            return
        }

        responseObserver.onNext(CarroResponse.newBuilder()
            .setId(carro.id!!)
            .build())
        responseObserver.onCompleted() // encerra conexão com o cliente
    }
}