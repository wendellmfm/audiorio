package br.great.excursaopajeu.util;


public class Fontes {

    public static  String getFonte(String nomePonto){
        switch (nomePonto.toUpperCase()){
            case Textos.RESERVATÓRIO_D_ÁGUA_DO_PAJEÚ:
                return "NOGUEIRA, Paulino. Presidentes do ceará. Período regencial. 7º presidente senador josé martiniano de alencar. Revista trimensal do instituto do ceará. Fortaleza, 1899, ano XIII, tomo XIII, 3º e 4º trimestres, p. 119-216. Disponível em: http://memoria.bn.br/DocReader/144843/3663";

            case Textos.BUEIRO_DA_ASSEMBLEIA:
                return "Obras Públicas. Codigo de Referência: BR APEC., Op. Co., Re. Enc. 14-19, Caixa 20. Repartição das Obras Publicas. Officios da Presidencia da Provincia. Encadernação 14, Livro 01, 12 de Fevereiro de 1866, p. 83. In: Arquivo Público do Ceará.";

            case Textos.CHAFARIZ_DO_PALÁCIO:
                return "Correio da Assemblea Provincial, Fortaleza, 22 de junho de 1839, n. 78, p. 02-06. Disponível em: http://memoria.bn.br/DocReader/DocReader.aspx?bib=262382&PagFis=107";

            case Textos.COLLECÇÃO_D_ÁGUAS:
                return "Comunicado. O Cearense, Fortaleza, 29 de abril de 1877, n. 37, ano XXXI, p.03. Disponível em: http://memoria.bn.br/DocReader/docreader.aspx?bib=709506&PagFis=11661";

            case Textos.PARNASO:
                return "Gazetilha. Cronica geral. Jornal da Fortaleza, fortaleza, 18 de junho de 1870, n. 115, ano II, p. 01. Disponível em: http://memoria.bn.br/DocReader/DocReader.aspx?bib=721247&PagFis=406";

            case Textos.PARQUE_PAJEÚ:
                return "Diário Oficial do Município. Ano LXI, Fortaleza, 21 de janeiro de 2014, N. 15.204. Decreto nº 13.290, de 14 de janeiro de 2014.";

            case Textos.EXALAÇÕES_MEPHETICAS:
                return "Parte Official. O Cearense, Fortaleza, 22 de Fevereiro de 1861, n. 1417, ano XV, p. 01. Disponível em: http://memoria.bn.br/DocReader/docreader.aspx?bib=709506&PagFis=4968";

            case Textos.RUÍNA_DO_AÇUDE:
                return "Publicação Solicitada. O Cearense, Fortaleza, 31 de Maio de 1866, N. 2100, Ano XX, p. 03. Disponível em: http://memoria.bn.br/DocReader/docreader.aspx?bib=709506&PagFis=7389";

            case Textos.PARQUE_DAS_ESCULTURAS:
                return "Oito Esculturas Somem do Parque Pajeú. Diário do Nordeste, Fortaleza, 19 de maio de 2009. Disponível em: http://diariodonordeste.verdesmares.com.br/cadernos/cidade/oito-esculturas-somem-do-parque-pajeu-1.723443";

            case Textos.POSTURAS_CAMARAIS_DE_1835:
                return "CAMPOS, Eduardo. A Fortaleza Provincial. Fortaleza, 1988. Disponível em: http://www.eduardocampos.jor.br/_livros/e04.pdf";

            case Textos.POSTURAS_CAMARAIS_DE_1844:
                return "CAMPOS, Eduardo. A Fortaleza Provincial. Fortaleza, 1988. Disponível em: http://www.eduardocampos.jor.br/_livros/e04.pdf";

            case Textos.PROIBIDO_APRESENTAR_SE_NU:
                return "CAMPOS, Eduardo. A Fortaleza Provincial. Fortaleza, 1988. Disponível em: http://www.eduardocampos.jor.br/_livros/e04.pdf";

            case Textos.PROJETO_CENTRO_BELO:
                return "Prefeitura Municipal de Fortaleza. Secretaria Municipal de Desenvolvimento Urbano e Infra-estrutura. Projeto de Urbanização do Riacho Pajeú, 2005.";

            case Textos.FORTE_DE_AREIA:
                return "Koster, Henry. Travels in Brazil. London, 1816, p.113.  Tradução nossa. Disponível em: https://archive.org/stream/travelsinbrazil00inkost#page/n7/mode/2up";

            case Textos.OBRA:
                return "Obras Públicas. Ofícios. Codigo de Referência: BR APEC., Op. Co., Ex. 01-06, Caixa 08. Dossiê 06, 07 de Abril de 1861. In: Arquivo Público do Ceará.";

            case Textos.PARQUE_J_DA_PENHA:
                return "Diário Oficial do Município. Ano LXI, Fortaleza, 21 de janeiro de 2014, N. 15.204. Decreto nº 13.290, de 14 de janeiro de 2014.";

            case Textos.TERCEIRO_PLANO:
                return "Barroso, Gustavo. Os mártires da confederação do equador. O Cruzeiro, Rio de Janeiro, 20 de abril de 1957, p. 52.\n" +
                        "Disponível em: http://docvirt.com/docreader.net/DocReader.aspx?bib=BibVirtMHN&PagFis=53953";

            case Textos.BELLO_RIO_DE_ÁGUA_DOCE:
                return "BECK, Matias [Mathias ou Mathijs]. “Diário da expedição de Matias Beck ao Ceará em 1649. Tradução do holandês por Alfredo de Carvalho” in Revista do Instituto do Ceará. Fortaleza, tomo XVII, 1903, pp. 325-405. Disponível em: https://pt.wikisource.org/wiki/Ficheiro:Diario_da_expedi%C3%A7%C3%A3o_de_Mathias_Beck.pdf#filelinks";

            case Textos.RELATORIO_MANSOUR:
                return "ELIAS, Mansour Daher. Levantamento preliminar da degradação das áreas de proteção do riacho Pajeú por Mansour Daher Elias, engenheiro civil da equipe de meio ambiente da secretaria executiva regional II da Prefeitura Municipal de Fortaleza. Fortaleza, 2000.";

            case Textos.RELATORIO_INSPECAO:
                return "NETO, Pedro Raimundo de Oliveira e NETO, Ataciso Cavalcante Mota. Relatório Técnico de Inspeção Nº 182/2013 da Equipe de Controle Ambiental da SEUMA. Fortaleza, 2013. In: Tribunal de Justiça do Estado do Ceará.";
        }

        return "";
    }

    public static  String getFonteURL(String nomePonto){
        switch (nomePonto.toUpperCase()){
            case Textos.RESERVATÓRIO_D_ÁGUA_DO_PAJEÚ:
                return "http://memoria.bn.br/DocReader/144843/3663";

            case Textos.BUEIRO_DA_ASSEMBLEIA:
                return "https://drive.google.com/open?id=1b3sNrVW_eiGFQMBMOVevgSIs5RBtlN4IsVFdoBrbfto";

            case Textos.CHAFARIZ_DO_PALÁCIO:
                return "http://memoria.bn.br/DocReader/DocReader.aspx?bib=262382&PagFis=107";

            case Textos.COLLECÇÃO_D_ÁGUAS:
                return "http://memoria.bn.br/DocReader/docreader.aspx?bib=709506&PagFis=11661";

            case Textos.PARNASO:
                return "http://memoria.bn.br/DocReader/DocReader.aspx?bib=721247&PagFis=406";

            case Textos.PARQUE_PAJEÚ:
                return "https://drive.google.com/open?id=0B6F0AzEGZKFKQTlWVWNSVzBMUlk";

            case Textos.EXALAÇÕES_MEPHETICAS:
                return "http://memoria.bn.br/DocReader/docreader.aspx?bib=709506&PagFis=4968";

            case Textos.RUÍNA_DO_AÇUDE:
                return "http://memoria.bn.br/DocReader/docreader.aspx?bib=709506&PagFis=7389";

            case Textos.PARQUE_DAS_ESCULTURAS:
                return "http://diariodonordeste.verdesmares.com.br/cadernos/cidade/oito-esculturas-somem-do-parque-pajeu-1.723443";

            case Textos.POSTURAS_CAMARAIS_DE_1835:
                return "http://www.eduardocampos.jor.br/_livros/e04.pdf";

            case Textos.POSTURAS_CAMARAIS_DE_1844:
                return "http://www.eduardocampos.jor.br/_livros/e04.pdf";

            case Textos.PROIBIDO_APRESENTAR_SE_NU:
                return "http://www.eduardocampos.jor.br/_livros/e04.pdf";

            case Textos.PROJETO_CENTRO_BELO:
                return "https://drive.google.com/open?id=0B6F0AzEGZKFKY2tjZDR5MUd5LUk";

            case Textos.FORTE_DE_AREIA:
                return "https://archive.org/stream/travelsinbrazil00inkost#page/n7/mode/2up";

            case Textos.OBRA:
                return "https://drive.google.com/open?id=1FIsqUQsi0gIlY_3NXCIGRf5mfRKG1xeLSoL2eH9eC5Q";

            case Textos.PARQUE_J_DA_PENHA:
                return "https://drive.google.com/open?id=0B6F0AzEGZKFKQTlWVWNSVzBMUlk";

            case Textos.TERCEIRO_PLANO:
                return "http://docvirt.com/docreader.net/DocReader.aspx?bib=BibVirtMHN&PagFis=53953";

            case Textos.BELLO_RIO_DE_ÁGUA_DOCE:
                return "https://drive.google.com/file/d/0B6F0AzEGZKFKU2trblI0bEhDV00/view";

            case Textos.RELATORIO_MANSOUR:
                return "https://drive.google.com/open?id=0B6F0AzEGZKFKZWVvVkxQVm85MTQ";

            case Textos.RELATORIO_INSPECAO:
                return "https://drive.google.com/open?id=0B6F0AzEGZKFKeldoMlpVbExvM2M";
        }

        return "";
    }
}
