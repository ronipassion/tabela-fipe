package br.com.alura.desafio_tabela_fipe.principal;

import br.com.alura.desafio_tabela_fipe.models.Dados;
import br.com.alura.desafio_tabela_fipe.models.Modelos;
import br.com.alura.desafio_tabela_fipe.models.Veiculos;
import br.com.alura.desafio_tabela_fipe.service.ConsumoApi;
import br.com.alura.desafio_tabela_fipe.service.ConverteDados;
import ch.qos.logback.core.encoder.JsonEscapeUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu() {
        System.out.println("Escolha uma das opções abaixo: ");
        System.out.println("carros \n motos \n caminhoes");
        var escolhaTipoVeiculo = leitura.nextLine();

        String endereco;

        if (escolhaTipoVeiculo.toLowerCase().contains("car")) {
            endereco = URL_BASE + "carros/marcas";
        } else if (escolhaTipoVeiculo.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Escolha o código da marca desejada");
        var codigoMarca = leitura.nextLine();
        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);

        System.out.println("\n Modelos dessa marca: ");
        var modeloLista = conversor.obterDados(json, Modelos.class);
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\n Digite um trecho do nome do veículo para busca");
        var nomeVeiculo = leitura.nextLine();
        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\n Modelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\n Digite o código do modelo: ");
        var codigoModelo = leitura.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);

        List<Veiculos> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculos veiculo = conversor.obterDados(json, Veiculos.class);
            veiculos.add(veiculo);
        }

        System.out.println("\n Todos os veículos filtrados por avaliação e ano: ");
        veiculos.forEach(System.out::println);

    }
}
