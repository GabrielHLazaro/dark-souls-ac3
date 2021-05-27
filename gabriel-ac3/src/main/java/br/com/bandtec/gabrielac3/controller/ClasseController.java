package br.com.bandtec.gabrielac3.controller;

import br.com.bandtec.gabrielac3.FilaObj;
import br.com.bandtec.gabrielac3.PilhaObj;
import br.com.bandtec.gabrielac3.dominio.GetAssincrono;
import br.com.bandtec.gabrielac3.dominio.RangedClasse;
import br.com.bandtec.gabrielac3.dominio.ResultadoRequisicao;
import br.com.bandtec.gabrielac3.dominio.TipoMagia;
import br.com.bandtec.gabrielac3.repository.RangedClasseRepository;
import br.com.bandtec.gabrielac3.repository.TipoMagiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/classes")
public class ClasseController {

    @Autowired
    private RangedClasseRepository repositoryClasse;

    @Autowired
    private TipoMagiaRepository repositoryTipo;

    private PilhaObj<RangedClasse> ultimoPost = new PilhaObj<>(99);

    private FilaObj<GetAssincrono> requisicoesAssincronas = new FilaObj(99);

    private List<ResultadoRequisicao> requisicoesTratadas = new ArrayList();

    private RangedClasse classeAntiga = new RangedClasse();
    private PilhaObj<RangedClasse> ultimoPut = new PilhaObj(99);

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
    public ResponseEntity postClasse(@RequestBody @Valid RangedClasse novaClasse){
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
                    "Use /fila para tratar as solicitações. O protocolo é: " + protocolo);
        }else {
            return ResponseEntity.status(404).body("Não existe não existe a Classe informada");
        }
    }

    @GetMapping("/fila")
    public ResponseEntity tratarClasseAssincrona(){
        if(!requisicoesAssincronas.isEmpty()){
            ResultadoRequisicao resultado = new ResultadoRequisicao(
                    requisicoesAssincronas.peek().getProtocolo(),
                    repositoryClasse.findById(requisicoesAssincronas.peek().getIdClasse()));
            requisicoesTratadas.add(resultado);
            repositoryClasse.findById(requisicoesAssincronas.poll().getIdClasse());
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

    @DeleteMapping
    public ResponseEntity desfazerPostClasse(){
        if(ultimoPost.isEmpty()){
            return ResponseEntity.status(204).build();
        }
        else{
            repositoryClasse.deleteById(ultimoPost.pop().getId());
            return ResponseEntity.status(200).body("A ultica classe inserida foi removida!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity putClasse(@PathVariable int id, @RequestBody RangedClasse alterarClasse){
        if(repositoryClasse.existsById(id)){
            classeAntiga.setId(id);
            classeAntiga.setNome(repositoryClasse.getById(id).getNome());
            classeAntiga.setConhecimento(repositoryClasse.getById(id).getConhecimento());
            classeAntiga.setInteligencia(repositoryClasse.getById(id).getInteligencia());
            classeAntiga.setFe(repositoryClasse.getById(id).getFe());
            classeAntiga.setCanalizador(repositoryClasse.getById(id).getCanalizador());
            classeAntiga.setTipoMagia(repositoryClasse.getById(id).getTipo());
            classeAntiga.setSoulLevel(repositoryClasse.getById(id).getSoulLevel());
            alterarClasse.setId(id);
            repositoryClasse.save(alterarClasse);
            return ResponseEntity.status(200).body("Tipo de magia alterado com sucesso!");
        }else {
            return ResponseEntity.status(404).body("Tipo de magia não encontrado");
        }
    }

    @PutMapping("/desfazer-alteracao")
    public ResponseEntity putDesfazerAlteracao(){
        if(ultimoPut.isEmpty()){
            return ResponseEntity.status(404).body("nenhuma alteração realizada");
        }else {
            repositoryClasse.save(classeAntiga);
            return ResponseEntity.status(201).body("Alteração desfeita!");
        }
    }

    @PostMapping("/importar-classe")
    public ResponseEntity importarClasse(@RequestParam MultipartFile arquivo) throws IOException {
        BufferedReader entrada = null;
        String registro;
        String tipoRegistro;
        String nome;
        Integer conhecimento;
        Integer inteligencia;
        Integer fe;
        String canalizador;
        Integer tipo;
        Double soulLevel;
        int contRegistro = 0;
        if(arquivo.isEmpty()){
            return ResponseEntity.badRequest().body("Arquivo não enviado!");
        }
        try {
            entrada = new BufferedReader(new FileReader(arquivo.getOriginalFilename()));
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
            ResponseEntity.status(400).body("Erro ao abrir arquivo!");
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
                else if (tipoRegistro.equals("02")) {
                    if (contRegistro == 0) {
                        System.out.println();
                        System.out.printf("%10s %12s %12s %3s %17s %13s %10s\n", "NOME",
                                "CONHECIMENTO", "INTELIGÊNCIA", "FÉ", "CANALIZADOR", "TIPO DE MAGIA", "SOUL LEVEL");

                    }
                    RangedClasse novaClasse = new RangedClasse();
                    nome = registro.substring(2,12).trim();
                    conhecimento = Integer.parseInt(registro.substring(12, 15));
                    inteligencia = Integer.parseInt(registro.substring(15, 18));
                    fe = Integer.parseInt(registro.substring(18, 21));
                    canalizador = registro.substring(21, 38).trim();
                    tipo = Integer.parseInt(registro.substring(38,41));
                    soulLevel = Double.parseDouble(registro.substring(41, 48)
                            .replace(',','.'));
                    System.out.printf("%10s %13d %13d %3d %17s %13d %10.2f\n", nome, conhecimento,
                            inteligencia, fe, canalizador, tipo, soulLevel);
                    novaClasse.setNome(nome);
                    novaClasse.setConhecimento(conhecimento);
                    novaClasse.setInteligencia(inteligencia);
                    novaClasse.setFe(fe);
                    novaClasse.setCanalizador(canalizador);
                    novaClasse.setTipoMagia(repositoryTipo.getById(tipo));
                    novaClasse.setSoulLevel(soulLevel);
                    contRegistro++;
                    try {
                        repositoryClasse.save(novaClasse);
                    } catch (Exception e){
                        return ResponseEntity.status(400).body("Erro ao salvar Classe: " + e.getMessage());
                    }
                    ultimoPost.push(novaClasse);
                }
                else if (tipoRegistro.equals("03")){
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
            ResponseEntity.status(400).body("Erro ao ler arquivo");
        }
        return ResponseEntity.created(null).build();
    }
}