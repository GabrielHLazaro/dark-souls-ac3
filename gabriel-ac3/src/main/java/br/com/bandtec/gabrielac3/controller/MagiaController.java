package br.com.bandtec.gabrielac3.controller;

import br.com.bandtec.gabrielac3.PilhaObj;
import br.com.bandtec.gabrielac3.dominio.TipoMagia;
import br.com.bandtec.gabrielac3.repository.TipoMagiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tipos-magia")
public class MagiaController {

    @Autowired
    private TipoMagiaRepository repositoryTipoMagia;
    private Integer novaRequisicao = 0;
    private PilhaObj<Integer> idRequisição = new PilhaObj<>(99);

    @GetMapping
    public ResponseEntity getTipoMagia(){
        if(repositoryTipoMagia.findAll().isEmpty()){
            return ResponseEntity.status(204).build();
        }else{
        return ResponseEntity.status(200).body(
                repositoryTipoMagia.findAll());
        }
    }

    @PostMapping
    public ResponseEntity postTipoMagia(@RequestBody TipoMagia novoTipo){
        repositoryTipoMagia.save(novoTipo);
        idRequisição.push(++novaRequisicao);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping
    public ResponseEntity deleteTipoMagia(){
        if(idRequisição.isEmpty()){
            return ResponseEntity.status(404).body("Desculpe, tipo de magia não encontrado!");
        }
        else{
            repositoryTipoMagia.deleteById(idRequisição.pop());
            return ResponseEntity.status(200).body("O ultimo tipo de magia inserido foi removido!");
        }
    }
}
