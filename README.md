[![GitHub release](https://img.shields.io/github/release/public-health-bioinformatics/irida-plugin-plasmid-screen.svg)](https://github.com/public-health-bioinformatics/irida-plugin-plasmid-screen/releases/latest)

# IRIDA Plasmid Screen Pipeline Plugin

![workflow.svg][]

# Table of Contents

   * [IRIDA Plasmid Screen Pipeline Plugin](#irida-mlst-pipeline-plugin)
   * [Installation](#buildingpackaging)
      * [Installing to IRIDA](#installing-to-irida)
      * [Installing Galaxy Dependencies](#installing-galaxy-dependencies)
   * [Usage](#usage)
      * [Monitoring Pipeline Status](#monitoring-pipeline-status)
      * [Analysis Results](#analysis-results)
      * [Metadata Table](#metadata-table)
   * [Building](#building)
      * [Installing IRIDA Libraries](#installing-irida-libraries)
      * [Building MLST Plugin](#building-mlst-plugin)

# Installation

## Installing to IRIDA

Please download the provided `irida-plugin-plasmid-screen-[version].jar` from the [releases][] page and copy to your `/etc/irida/plugins` directory.  Now you may start IRIDA and you should see the pipeline appear in your list of pipelines.

*Note:* This plugin requires you to be running IRIDA version >= `19.01`. Please see the [IRIDA][] documentation for more details.

## Installing Galaxy Dependencies

In order to use this pipeline, you will also have to install several Galaxy tools within your Galaxy instance. These can be found at:

| Name               | Version     | Galaxy Tool                                                               |
|--------------------|-------------|---------------------------------------------------------------------------|
| mash               | `2.1.1`     | <https://toolshed.g2.bx.psu.edu/view/iuc/mash/402b67d1af7d >              |
| bwa                | `0.7.17`    | <https://toolshed.g2.bx.psu.edu/view/devteam/bwa/01ac0a5fedc3>            |
| samtools_fixmate   | `1.9`       | <https://toolshed.g2.bx.psu.edu/view/iuc/samtools_fixmate/bc0cc7bfbfe9>   |
| freebayes          | `1.1.0.46`  | <https://toolshed.g2.bx.psu.edu/view/devteam/freebayes/156b60c1530f>      |

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
[workflow.svg]: doc/images/workflow.svg
[releases]: https://github.com/public-health-bioinformatics/irida-plugin-plasmid-screen/releases
