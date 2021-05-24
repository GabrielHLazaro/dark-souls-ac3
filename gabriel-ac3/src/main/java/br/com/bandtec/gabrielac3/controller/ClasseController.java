package br.com.bandtec.gabrielac3.controller;

import br.com.bandtec.gabrielac3.FilaObj;
import br.com.bandtec.gabrielac3.PilhaObj;
import br.com.bandtec.gabrielac3.dominio.GetAssincrono;
import br.com.bandtec.gabrielac3.dominio.RangedClasse;
import br.com.bandtec.gabrielac3.dominio.TipoMagia;
import br.com.bandtec.gabrielac3.repository.RangedClasseRepository;
import br.com.bandtec.gabrielac3.repository.TipoMagiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/classes")
public class ClasseController {

    @Autowired
    private RangedClasseRepository repositoryClasse;
    private PilhaObj<RangedClasse> ultimoPost = new PilhaObj<>(99);
    private PilhaObj<RangedClasse> alteracaoClasse = new PilhaObj<>(99);
    private FilaObj<GetAssincrono> requisicoesAssincronas = new FilaObj(99);
    private int protocolo = 0;

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
        ultimoPost.push(novaClasse);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity pesquisaClasseAssincrona(@PathVariable int id){
        if(repositoryClasse.findById(id).isPresent()){
            GetAssincrono novaRequisicao = new GetAssincrono(++protocolo, id);
            requisicoesAssincronas.insert(novaRequisicao);
        return ResponseEntity.status(200).body("Solicitação adicionada na fila! " +
                "Use /fila para tratar as solicitações.");
        }else {
            return ResponseEntity.status(204).build();
        }
    }

    @GetMapping("/fila")
    public ResponseEntity tratarClasseAssincrona(){
        if(protocolo > 0){
            return ResponseEntity.status(200).body(repositoryClasse.findById(requisicoesAssincronas.poll().getIdClasse()));
        }
        else {
            return ResponseEntity.status(204).body("Não existem solicitações na fila!");
        }
    }

    @DeleteMapping
    public ResponseEntity desfazerPostClasse(){
        if(ultimoPost.isEmpty()){
            return ResponseEntity.status(404).body("Desculpe, classe não encontrada!");
        }
        else{
            repositoryClasse.deleteById(ultimoPost.pop().getId());
            return ResponseEntity.status(200).body("A ultica classe inserida foi removida!");
        }
    }

//    @PutMapping("/{id}")
//    public ResponseEntity putClasse(@PathVariable int id, @RequestBody RangedClasse classeAlterada){
//        Optional<RangedClasse> opicional = repositoryClasse.findById(id);
//        if(opicional.isPresent()){
//            RangedClasse classeParaAlterar = repositoryClasse.getOne(id);
//            alteracaoClasse.push(classeParaAlterar);
//            classeParaAlterar.setNome(classeAlterada.getNome());
//            classeParaAlterar.setConhecimento(classeAlterada.getConhecimento());
//            classeParaAlterar.setInteligencia(classeAlterada.getInteligencia());
//            classeParaAlterar.setFe(classeAlterada.getFe());
//            classeParaAlterar.setCanalizador(classeAlterada.getCanalizador());
//            classeParaAlterar.setTipoMagia(classeAlterada.getTipo());
//            classeParaAlterar.setSoulLevel(classeAlterada.getSoulLevel());
//            repositoryClasse.save();
//        }
//    }

//    @PutMapping("/desfazer-alteracao")
//    public ResponseEntity desfazerPutClasse(){
//
//    }
}
