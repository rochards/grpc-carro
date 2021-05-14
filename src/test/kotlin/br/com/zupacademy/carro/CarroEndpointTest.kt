package br.com.zupacademy.carro

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false) // é semelhante ao @SpringBootTest
internal class CarroEndpointTest {

    @Inject
    lateinit var carroRepository: CarroRepository

    @Inject
    lateinit var grpcClient: GrpcCarroServiceGrpc.GrpcCarroServiceBlockingStub

    @BeforeEach
    internal fun setUp() {
        carroRepository.deleteAll()
    }

    @Test
    @DisplayName("deve adicionar um novo carro")
    fun adicionaTeste01() {
        val request = CarroRequest.newBuilder()
            .setModelo("Ford Ranger")
            .setPlaca("ABC-1234")
            .build()

        val response = grpcClient.adiciona(request)

        with(response) {
            assertNotNull(id)
            assertTrue(carroRepository.existsById(id))
        }
    }

    @Test
    @DisplayName("não deve adicionar carros com placas iguais")
    fun adicionaTeste02() {
        carroRepository.save(Carro("Ford Ranger", "ABC-1234"))
        val request = CarroRequest.newBuilder()
            .setModelo("Ford Fusion")
            .setPlaca("ABC-1234")
            .build()

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.adiciona(request)
        }

        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("carro com placa existente", status.description)
        }
    }

    @Test
    @DisplayName("não deve adicionar novo caso caso os dados de entrada forem inválidos")
    fun adicionaTeste03() {
        val request = CarroRequest.newBuilder()
            .setModelo("")
            .setPlaca("")
            .build()

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.adiciona(request)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("dados de entrada inválidos", status.description)
        }
    }

    @Factory
    class Client {
        /* em @GrpcChannel("endereco:porta") vc deveria passar o endereço do servidor gRPC e a porta, porém a
        anotação @MicronautTest levanta o servidor em uma porta aleatória, tornando assim praticamente impossível
        passar essas informações. Passando então apenas grpc-server ou GrpcServerChannel.NAME isso fica transparente para vc. */
        @Singleton
        fun blockingStub(
            @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel
        ): GrpcCarroServiceGrpc.GrpcCarroServiceBlockingStub? {
            return GrpcCarroServiceGrpc.newBlockingStub(channel)
        }
    }
}