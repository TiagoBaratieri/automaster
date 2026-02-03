package com.baratieri.automaster.dto.request;

import com.baratieri.automaster.entities.Cliente;
import com.baratieri.automaster.entities.Endereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record ClienteRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O CPF/CNPJ é obrigatório")
        @Pattern(regexp = "\\d{11}|\\d{14}", message = "CPF/CNPJ inválido (apenas números)")
        String cpfOuCnpj,

        @NotBlank(message = "O telefone é obrigatório")
        String telefone,

        @Email(message = "E-mail inválido")
        String email,

        @NotNull(message = "Os dados de endereço são obrigatórios")
        @Valid
        EnderecoRequestDTO endereco
) {

    public Cliente toEntity() {
        Cliente cliente = new Cliente();

        cliente.setNome(this.nome());
        cliente.setCpfOuCnpj(this.cpfOuCnpj());
        cliente.setTelefone(this.telefone());
        cliente.setEmail(this.email());

        Endereco endereco = new Endereco();
        endereco.setLogradouro(this.endereco().logradouro()); // Acessa o sub-DTO
        endereco.setBairro(this.endereco().bairro());
        endereco.setComplemento(this.endereco().complemento());
        endereco.setNumero(this.endereco().numero());
        endereco.setCep(this.endereco().cep());
        endereco.setEstado(this.endereco().estado());
        endereco.setCidade(this.endereco().cidade());

        cliente.setEndereco(endereco);

        return cliente;
    }
}
