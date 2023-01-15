package com.dev.backend.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.backend.entity.Pessoa;
import com.dev.backend.repository.PessoaRepository;

@Service
public class PessoaGerenciamentoService {
    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private EmailService emailService;

    public String solicitarCodigo(String email){
        Pessoa pessoa = pessoaRepository.findByEmail(email);
        pessoa.setCodigoRecuperacaoSenha(getCodigoRecuperacaoSenha(pessoa.getId()));
        pessoa.setDataEnvioCodigo(new Date());
        pessoaRepository.saveAndFlush(pessoa);
        emailService.enviarEmailTexto(
            pessoa.getEmail(), "Código de Recuperação de Senha", "Olá, o seu código de recuperação de senha é o seguinte: "+pessoa.getCodigoRecuperacaoSenha());
        return "Código Enviado com Sucesso!";
    }  

    public String alterarSenha(Pessoa pessoa){

        Pessoa pessoaCadastrada = pessoaRepository.findByEmailAndCodigoRecuperacaoSenha(pessoa.getEmail(), pessoa.getCodigoRecuperacaoSenha());

        if(pessoaCadastrada != null){    
            Date diferenca = new Date(new Date().getTime() - pessoaCadastrada.getDataEnvioCodigo().getTime());

            if(diferenca.getTime()/1000<900){
                //Adicionar Spring Security para criptrografar a senha
                pessoaCadastrada.setSenha(pessoa.getSenha());
                pessoaCadastrada.setCodigoRecuperacaoSenha(null);
                pessoaRepository.saveAndFlush(pessoaCadastrada);
                return "Senha alterada com Sucesso!";
            }else{
                return "Tempo expirado, solicite um novo código na loja.";
            }
        }else{
            return "Email ou código não localizado!";
        }

    }

    private String getCodigoRecuperacaoSenha(Long id){
        DateFormat format = new SimpleDateFormat("ddMMyyyyHHmmssmm");
        return format.format(new Date())+id;
    }
}
