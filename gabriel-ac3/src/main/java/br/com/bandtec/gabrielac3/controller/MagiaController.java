package br.com.bandtec.gabrielac3.controller;

import br.com.bandtec.gabrielac3.FilaObj;
import br.com.bandtec.gabrielac3.PilhaObj;
import br.com.bandtec.gabrielac3.dominio.GetAssincrono;
import br.com.bandtec.gabrielac3.dominio.RangedClasse;
import br.com.bandtec.gabrielac3.dominio.ResultadoRequisicao;
import br.com.bandtec.gabrielac3.dominio.TipoMagia;
import br.com.bandtec.gabrielac3.repository.TipoMagiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tipos-magia")
public class MagiaController {

    @Autowired
    private TipoMagiaRepository repositoryTipoMagia;
    private PilhaObj<TipoMagia> ultimoPost = new PilhaObj<>(99);

    private PilhaObj<TipoMagia> ultimaAlteracao = new PilhaObj<>(99);

    private FilaObj<GetAssincrono> requisicoesAssincronas = new FilaObj(99);

    private List<ResultadoRequisicao> requisicoesTratadas = new ArrayList();

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
            ResultadoRequisicao resultado = new ResultadoRequisicao(
                    requisicoesAssincronas.peek().getProtocolo(),
                    repositoryTipoMagia.findById(requisicoesAssincronas.peek().getIdClasse()));
            requisicoesTratadas.add(resultado);
            repositoryTipoMagia.findById(requisicoesAssincronas.poll().getIdClasse());
            return ResponseEntity.status(200).body("Solicitação " + resultado.getProtocolo() +
                    " foi tratada");
        }
        else {
            return ResponseEntity.status(204).body("Não existem solicitações na fila!");
        }
    }

    @GetMapping("/verificar-solicitacao/{id}")
    public ResponseEntity devolverTratamentoAssincrono(@PathVariable Integer id){
        return ResponseEntity.status(200).body(requisicoesTratadas.stream().filter(resultadoRequisicao ->
                resultadoRequisicao.getProtocolo().equals(id)).collect(Collectors.toList()));
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

    @PostMapping("/importar-tipo")
    public ResponseEntity importarTipo(@RequestParam MultipartFile arquivo) throws IOException {
        BufferedReader entrada = null;
        String registro;
        String tipoRegistro;
        String nome;
        int contRegistro = 0;
        if(arquivo.isEmpty()){
            return ResponseEntity.badRequest().body("Arquivo não enviado!");
        }
        try {
            entrada = new BufferedReader(new FileReader(arquivo.getOriginalFilename()));
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
            return ResponseEntity.status(400).body("Erro ao abrir arquivo");
        }
        try {
            registro = entrada.readLine();

            while (registro != null) {
                tipoRegistro = registro.substring(0, 2);
                if (tipoRegistro.equals("00")) {
                    System.out.println("Header");
                    System.out.println("Tipo de arquivo: " + registro.substring(2, 8));
                    System.out.println("Ra do aluno: " + registro.substring(8, 16));
                    System.out.println("Data/hora de geração do arquivo: " + registro.substring(16,35));
                    System.out.println("Versão do layout: " + registro.substring(35,37));
                }
                else if (tipoRegistro.equals("01")) {
                    System.out.println("\nTrailer");
                    int qtdRegistro = Integer.parseInt(registro.substring(2,7));
                    if (qtdRegistro == contRegistro) {
                        System.out.println("Quantidade de registros gravados compatível com quantidade lida");
                    }
                    else {
                        System.out.println("Quantidade de registros gravados não confere com quantidade lida");
                    }
                }
                else if (tipoRegistro.equals("03")) {
                    if (contRegistro == 0) {
                        System.out.println();
                        System.out.printf("%20s\n", "NOME");

                    }
                    TipoMagia novoTipo = new TipoMagia();
                    nome = registro.substring(2,22).trim();
                    System.out.printf("%20s\n", nome);
                    novoTipo.setNome(nome);
                    contRegistro++;
                    repositoryTipoMagia.save(novoTipo);
                }
                else if (tipoRegistro.equals("02")){
                    contRegistro++;
                }
                else {
                    System.out.println("Tipo de registro inválido");
                }
                registro = entrada.readLine();
            }
            entrada.close();
        } catch (IOException e) {
            System.err.printf("Erro ao ler arquivo: %s.\n", e.getMessage());
            ResponseEntity.status(400).body("Erro ao ler arquivo!");
        }
        return ResponseEntity.status(201).body("Tipos de magia adicionados!");
    }
}