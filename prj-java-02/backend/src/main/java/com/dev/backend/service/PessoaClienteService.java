package com.dev.backend.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.backend.dto.PessoaClienteRequestDTO;
import com.dev.backend.entity.Pessoa;
import com.dev.backend.repository.PessoaClienteRepository;

@Service
public class PessoaClienteService {
    @Autowired
    private PessoaClienteRepository pessoaRepository;

    @Autowired
    private PermissaoPessoaService permissaoPessoaService;

    @Autowired
    private EmailService emailService;

    public Pessoa registrar(PessoaClienteRequestDTO pessoaClienteRequestDTO){
        Pessoa pessoa = new PessoaClienteRequestDTO().converter(pessoaClienteRequestDTO);
        pessoa.setDataCriacao(new Date());
        Pessoa objetoNovo = pessoaRepository.saveAndFlush(pessoa);
        permissaoPessoaService.vincularPessoaPermissaoCliente(objetoNovo);
        //emailService.enviarEmailTexto(
        //    pessoaNovo.getEmail(), "Cadastro na Loja Virtual", "Cadastro na Loja realizado com Sucesso. Em breve você receberá a senha por e-mail."
        //    );
        Map<String, Object> propMap = new HashMap<>();
        propMap.put("nome", objetoNovo.getNome());
        propMap.put("mensagem", "Cadastro na Loja realizado com Sucesso. Em breve você receberá a senha por e-mail.");

        emailService.enviarEmailTemplate(objetoNovo.getEmail(), "Cadastrado na Loja Virtual", propMap);
        return objetoNovo;
    }  
}
