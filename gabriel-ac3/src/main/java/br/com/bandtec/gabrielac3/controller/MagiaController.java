package br.com.bandtec.gabrielac3.controller;

import br.com.bandtec.gabrielac3.FilaObj;
import br.com.bandtec.gabrielac3.PilhaObj;
import br.com.bandtec.gabrielac3.dominio.GetAssincrono;
import br.com.bandtec.gabrielac3.dominio.RangedClasse;
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
    private PilhaObj<TipoMagia> ultimoPost = new PilhaObj<>(99);

    private PilhaObj<TipoMagia> ultimaAlteracao = new PilhaObj<>(99);

    private FilaObj<GetAssincrono> requisicoesAssincronas = new FilaObj(99);

    private int protocolo = 0;

    @GetMapping
    public ResponseEntity getTipoMagia(){
        if(repositoryTipoMagia.findAll().isEmpty()){
            return ResponseEntity.status(204).build();
        }else{
        return ResponseEntity.status(200).body(
                repositoryTipoMagia.findAll());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity pesquisaClasseAssincrona(@PathVariable int id){
        if(repositoryTipoMagia.findById(id).isPresent()){
            GetAssincrono novaRequisicao = new GetAssincrono(++protocolo, id);
            requisicoesAssincronas.insert(novaRequisicao);
            return ResponseEntity.status(200).body("Solicitação adicionada na fila! " +
                    "Use /fila para tratar as solicitações. O protocolo é: " + protocolo);
        }else {
            return ResponseEntity.status(204).build();
        }
    }

    @GetMapping("/fila")
    public ResponseEntity tratarClasseAssincrona(){
        if(!requisicoesAssincronas.isEmpty()){
            return ResponseEntity.status(200).body(repositoryTipoMagia.findById(requisicoesAssincronas.poll().getIdClasse()));
        }
        else {
            return ResponseEntity.status(204).body("Não existem solicitações na fila!");
        }
    }

    @PostMapping
    public ResponseEntity postTipoMagia(@RequestBody TipoMagia novoTipo){
        repositoryTipoMagia.save(novoTipo);
        ultimoPost.push(novoTipo);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping
    public ResponseEntity deleteTipoMagia(){
        if(ultimoPost.isEmpty()){
            return ResponseEntity.status(404).body("Desculpe, tipo de magia não encontrado!");
        }
        else{
            repositoryTipoMagia.deleteById(ultimoPost.pop().getId());
            return ResponseEntity.status(200).body("O ultimo tipo de magia inserido foi removido!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity putTipoMagia(@PathVariable int id, @RequestBody TipoMagia alterarTipo){
        if(repositoryTipoMagia.existsById(id)){
            alterarTipo.setId(id);
            ultimaAlteracao.push(repositoryTipoMagia.getOne(id));
            repositoryTipoMagia.save(alterarTipo);
            ultimaAlteracao.exibe();
            return ResponseEntity.status(200).body("Tipo de magia alterado com sucesso!");
        }else {
            return ResponseEntity.status(404).body("Tipo de magia não encontrado");
        }
    }

    @GetMapping("/pilha")
    public ResponseEntity getPilha(){
        return ResponseEntity.status(200).body(ultimaAlteracao.peek().getNome());
    }
}
