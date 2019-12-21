[![GitHub release](https://img.shields.io/github/release/public-health-bioinformatics/irida-plugin-plasmid-screen.svg)](https://github.com/public-health-bioinformatics/irida-plugin-plasmid-screen/releases/latest)

# IRIDA Plasmid Screen Pipeline Plugin

![galaxy-workflow-diagram.png][]

# Table of Contents

   * [IRIDA Plasmid Screen Pipeline Plugin](#irida-plasmid-screen-pipeline-plugin)
   * [Installation](#buildingpackaging)
      * [Installing to IRIDA](#installing-to-irida)
      * [Installing Galaxy Dependencies](#installing-galaxy-dependencies)
   * [Usage](#usage)
      * [Monitoring Pipeline Status](#monitoring-pipeline-status)
      * [Analysis Results](#analysis-results)
      * [Metadata Table](#metadata-table)
   * [Building](#building)
      * [Installing IRIDA Libraries](#installing-irida-libraries)
      * [Building Plasmid-Screen Plugin](#building-plasmid-screen-plugin)

# Installation

## Installing to IRIDA

Please download the provided `irida-plugin-plasmid-screen-[version].jar` from the [releases][] page and copy to your `/etc/irida/plugins` directory.  Now you may start IRIDA and you should see the pipeline appear in your list of pipelines.

*Note:* This plugin requires you to be running IRIDA version >= `19.01`. Please see the [IRIDA][] documentation for more details.

## Installing Galaxy Dependencies

In order to use this pipeline, you will also have to install several Galaxy tools within your Galaxy instance. These can be found at:

| Name                               | Version         | Owner                          | Metadata Revision | Galaxy Toolshed Link                                                                                                                                                    |
|------------------------------------|-----------------|--------------------------------|-------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| unicycler                          | `0.4.8`         | `iuc`                          | 7 (2019-10-08)    | [unicycler-7:88c240872a65](https://toolshed.g2.bx.psu.edu/view/iuc/unicycler/88c240872a65)                                                                              |
| quast                              | `5.0.2`         | `iuc`                          | 5 (2018-12-04)    | [quast-5:81df4950d65b](https://toolshed.g2.bx.psu.edu/view/iuc/quast/81df4950d65b)                                                                                      |
| mob_suite                          | `2.0.5`         | `nml`                          | 6 (2019-12-11)    | [mob_suite-6:9424de64bfa8](https://toolshed.g2.bx.psu.edu/view/nml/mob_suite/9424de64bfa8)                                                                              |
| abricate                           | `0.9.8`         | `iuc`                          | 7 (2019-10-29)    | [abricate-7:4efdca267d51](https://toolshed.g2.bx.psu.edu/view/iuc/abricate/4efdca267d51)                                                                                |
| pick_plasmids_containing_genes     | `0.2.0`         | `public-health-bioinformatics` | 1 (2019-12-19)    | [pick_plasmids_containing_genes-1:c9129ecc609d](https://toolshed.g2.bx.psu.edu/view/public-health-bioinformatics/pick_plasmids_containing_genes/c9129ecc609d)           |
| screen_abricate_report             | `0.2.0`         | `public-health-bioinformatics` | 2 (2019-12-19)    | [screen_abricate_report-2:912a3a3dc082](https://toolshed.g2.bx.psu.edu/view/public-health-bioinformatics/screen_abricate_report/912a3a3dc082)                           |
| collapse_collections               | `4.1`           | `nml`                          | 5 (2019-08-27)    | [collapse_collections-5:33151a38533a](https://toolshed.g2.bx.psu.edu/view/nml/collapse_collections/33151a38533a)                                                        |
| combine_tabular_collection         | `0.1`           | `nml`                          | 0 (2017-02-06)    | [combine_tabular_collection-0:b815081988b5](https://toolshed.g2.bx.psu.edu/view/nml/combine_tabular_collection/b815081988b5)                                                        |
| bundle_collections                 | `1.2.1`         | `nml`                          | 1 (2017-08-04)    | [bundle_collections-1:cd6da887a5f4](https://toolshed.g2.bx.psu.edu/view/nml/bundle_collections/cd6da887a5f4)                                                            |
| text_processing                    | (multiple)      | `bgruening`                    | 13 (2019-04-03)   | [text_processing-13:0a8c6b61f0f4](https://toolshed.g2.bx.psu.edu/view/bgruening/text_processing/0a8c6b61f0f4)                                                           |


# Usage

## Monitoring Pipeline Status

To monitor the status of the launched pipeline, please select the **Analyses > Your Analyses** menu.

![your-analyses.png][]

From here, you can monitor the status of your pipeline.

![monitor-analyses.png][]

## Analysis Results

Once the analysis pipeline is finished, you can view the analysis results in your browser or download the files to your machine.

![results.png][]

These results include ...

## Metadata Table

If you selected the **Save Results to Project Line List Metadata** option when launching the pipeline...

![metadata.png][]

# Building

## Installing IRIDA Libraries

To build this plugin yourself, you must first install [IRIDA][] to your local Maven repository. Please make sure you are installing the IRIDA version defined in the `irida.version.compiletime` property in the [pom.xml][] file (e.g., `19.01.3`). Or, alternatively, please update the IRIDA dependency version in the `pom.xml` file.

To install the IRIDA libraries to a local Maven repository, please run the following from within the [IRIDA][] project (the `irida/` directory):

```bash
mvn clean install -DskipTests
```

## Building MLST Plugin

Once IRIDA is installed, you may build the pipeline plugin by running the following in this project's directory (the `irida-plugin-plasmid-screen` directory):

```bash
mvn clean package
```

This should produce a `target/*.jar` file, which can be copied into `/etc/irida/plugins/`.


[maven]: https://maven.apache.org/
[IRIDA]: http://irida.ca/
[Galaxy]: https://galaxyproject.org/
[Java]: https://www.java.com/
[irida-pipeline]: https://irida.corefacility.ca/documentation/developer/tools/pipelines/
[irida-pipeline-galaxy]: https://irida.corefacility.ca/documentation/developer/tools/pipelines/#galaxy-workflow-development
[irida-wf-ga2xml]: https://github.com/phac-nml/irida-wf-ga2xml
[pom.xml]: pom.xml
[workflows-dir]: src/main/resources/workflows
[workflow-structure]: src/main/resources/workflows/0.1.0/irida_workflow_structure.ga
[irida-plugin-java]: https://github.com/phac-nml/irida/tree/development/src/main/java/ca/corefacility/bioinformatics/irida/plugins/IridaPlugin.java
[irida-setup]: https://irida.corefacility.ca/documentation/administrator/index.html
[properties]: https://en.wikipedia.org/wiki/.properties
[messages]: src/main/resources/workflows/0.1.0/messages_en.properties
[your-analyses.png]: doc/images/your-analyses.png
[monitor-analyses.png]: doc/images/monitor-analyses.png
[results.png]: doc/images/results.png
[pipeline.png]: doc/images/pipeline.png
[metadata.png]: doc/images/metadata.png
[galaxy-workflow-diagram.png]: doc/images/galaxy-workflow-diagram.png
[releases]: https://github.com/public-health-bioinformatics/irida-plugin-plasmid-screen/releases
