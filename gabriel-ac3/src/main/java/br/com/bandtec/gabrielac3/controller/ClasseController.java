package br.com.bandtec.gabrielac3.controller;

import br.com.bandtec.gabrielac3.PilhaObj;
import br.com.bandtec.gabrielac3.dominio.RangedClasse;
import br.com.bandtec.gabrielac3.dominio.TipoMagia;
import br.com.bandtec.gabrielac3.repository.RangedClasseRepository;
import br.com.bandtec.gabrielac3.repository.TipoMagiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/classes")
public class ClasseController {

    @Autowired
    private RangedClasseRepository repositoryClasse;
    private Integer novaRequisicao = 0;
    private PilhaObj<Integer> idRequisição = new PilhaObj<>(99);

    @GetMapping
    public ResponseEntity getClasses(){
        if (repositoryClasse.findAll().isEmpty()){
            return ResponseEntity.status(204).build();
        }else {
            return ResponseEntity.status(200).body(
                    repositoryClasse.findAll());
        }
    }

    @PostMapping
    public ResponseEntity postClasse(@RequestBody RangedClasse novaClasse){
        repositoryClasse.save(novaClasse);
        idRequisição.push(++novaRequisicao);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity pesquisarClasse(@PathVariable int id){
        if(repositoryClasse.findById(id).isPresent()){
        return ResponseEntity.status(200).body(repositoryClasse.findById(id));
        }else {
            return ResponseEntity.status(204).build();
        }
    }

    @DeleteMapping
    public ResponseEntity desfazerPostClasse(){
        if(idRequisição.isEmpty()){
            return ResponseEntity.status(404).body("Desculpe, classe não encontrada!");
        }
        else{
            repositoryClasse.deleteById(idRequisição.pop());
            return ResponseEntity.status(200).body("A ultica classe inserida foi removida!");
        }
    }
}
