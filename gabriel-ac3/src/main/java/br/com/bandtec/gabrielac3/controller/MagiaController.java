package br.com.bandtec.gabrielac3.controller;

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
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTipoMagia(@PathVariable int id){
        if(repositoryTipoMagia.findById(id).isPresent()){
            repositoryTipoMagia.deleteById(id);
            return ResponseEntity.status(200).build();
        }else{
            return ResponseEntity.status(404).body("Desculpe, tipo de magia não encontrado!");
        }
    }
}
