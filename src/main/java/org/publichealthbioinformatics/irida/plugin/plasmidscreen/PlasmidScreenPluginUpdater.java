package org.publichealthbioinformatics.irida.plugin.plasmidscreen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.PipelineProvidedMetadataEntry;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.AnalysisSampleUpdater;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import org.apache.jute.compiler.JField;

/**
 * This implements a class used to perform post-processing on the analysis
 * pipeline results to extract information to write into the IRIDA metadata
 * tables. Please see
 * <https://github.com/phac-nml/irida/blob/development/src/main/java/ca/corefacility/bioinformatics/irida/pipeline/results/AnalysisSampleUpdater.java>
 * or the README.md file in this project for more details.
 */
public class PlasmidScreenPluginUpdater implements AnalysisSampleUpdater {

    private final MetadataTemplateService metadataTemplateService;
    private final SampleService sampleService;
    private final IridaWorkflowsService iridaWorkflowsService;

    /**
     * Builds a new {@link PlasmidScreenPluginUpdater} with the given services.
     *
     * @param metadataTemplateService The metadata template service.
     * @param sampleService           The sample service.
     * @param iridaWorkflowsService   The irida workflows service.
     */
    public PlasmidScreenPluginUpdater(MetadataTemplateService metadataTemplateService, SampleService sampleService,
                                      IridaWorkflowsService iridaWorkflowsService) {
        this.metadataTemplateService = metadataTemplateService;
        this.sampleService = sampleService;
        this.iridaWorkflowsService = iridaWorkflowsService;
    }

    /**
     * Code to perform the actual update of the {@link Sample}s passed in the
     * collection.
     *
     * @param samples  A collection of {@link Sample}s that were passed to this
     *                 pipeline.
     * @param analysisSubmission The {@link AnalysisSubmission} object corresponding to this
     *                 analysis submission.
     */
    @Override
    public void update(Collection<Sample> samples, AnalysisSubmission analysisSubmission) throws PostProcessingException {
        if (samples == null) {
            throw new IllegalArgumentException("samples is null");
        } else if (analysisSubmission == null) {
            throw new IllegalArgumentException("analysis is null");
        } else if (samples.size() != 1) {
            // In this particular pipeline, only one sample should be run at a time so I
            // verify that the collection of samples I get has only 1 sample
            throw new IllegalArgumentException(
                    "samples size=" + samples.size() + " is not 1 for analysisSubmission=" + analysisSubmission.getId());
        }

        // extract the 1 and only sample (if more than 1, would have thrown an exception
        // above)
        final Sample sample = samples.iterator().next();

        // extracts paths to the analysis result files
        AnalysisOutputFile mlstAnalysisFile = analysisSubmission.getAnalysis().getAnalysisOutputFile("mlst");
        Path mlstFile = mlstAnalysisFile.getFile();

        try {
            Map<String, MetadataEntry> metadataEntries = new HashMap<>();

            // get information about the workflow (e.g., version and name)
            IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(analysisSubmission.getWorkflowId());
            String workflowVersion = iridaWorkflow.getWorkflowDescription().getVersion();
            String workflowName = iridaWorkflow.getWorkflowDescription().getName();

            // gets information from the "plasmids_mob_typer_report.tsv" output file and constructs metadata
            // objects
            List<Map<String, String>> mobTyperReport = parseMobTyperReportFile(mlstFile);

            // TODO: complete logic for what to store in metadata table

            Map<MetadataTemplateField, MetadataEntry> metadataMap = metadataTemplateService
                    .getMetadataMap(metadataEntries);

            // merges with existing sample metadata
            sample.mergeMetadata(metadataMap);

            // does an update of the sample metadata
            sampleService.updateFields(sample.getId(), ImmutableMap.of("metadata", sample.getMetadata()));
        } catch (IOException e) {
            throw new PostProcessingException("Error parsing hash file", e);
        } catch (IridaWorkflowNotFoundException e) {
            throw new PostProcessingException("Could not find workflow for id=" + analysisSubmission.getWorkflowId(), e);
        }
    }

    /**
     * Parses out values from the MLST output file into a {@link Map} linking 'mlstField' to
     * 'mlstValue'.
     *
     * @param mobTyperReportFilePath The {@link Path} to the file containing the hash values from
     *                 the pipeline. This file should contain contents like:
     *
     *                 <pre>
     * file_id	num_contigs	total_length	gc	rep_type(s)	rep_type_accession(s)	relaxase_type(s)	relaxase_type_accession(s)	mpf_type	mpf_type_accession(s)	orit_type(s)	orit_accession(s)	PredictedMobility	mash_nearest_neighbor	mash_neighbor_distance	mash_neighbor_cluster	NCBI-HR-rank	NCBI-HR-Name	LitRepHRPlasmClass	LitPredDBHRRank	LitPredDBHRRankSciName	LitRepHRRankInPubs	LitRepHRNameInPubs	LitMeanTransferRate	LitClosestRefAcc	LitClosestRefDonorStrain	LitClosestRefRecipientStrain	LitClosestRefTransferRate	LitClosestConjugTemp	LitPMIDs	LitPMIDsNumber
     * plasmid_1068	2	19016	50.54690786705932	-	-	-	-	-	-	-	-	Non-mobilizable	CP021680	0.00705245	1068	-	-	-	-	-	-	-	-	-	-	-	-	-	-	-
     * plasmid_1550	5	106133	51.586217293396025	IncFIIA,IncFII,IncFIA	000136__AP014877_00014,000121__CP024805,000094__NZ_CP015070_00117	MOBF	NC_017627_00068	MPF_F	08-5333_00200,NC_008460_00107,NC_014615_00033,NC_010488_00021,NC_018966_00040,NC_017639_00100,NC_007675_00027,NC_013437_00116,NC_017639_00094,NC_019094_00090,NC_010409_00124,NC_022651_00077	-	-	Conjugative	CP011064	0.00476862	1550	-	-	-	-	-	-	-	-	-	-	-	-	-	-	-
     *                 </pre>
     *
     * @return A {@link Map} linking 'mlstField' to 'mlstValue'.
     * @throws IOException             If there was an error reading the file.
     * @throws PostProcessingException If there was an error parsing the file.
     */
    @VisibleForTesting
    List<Map<String, String>> parseMobTyperReportFile(Path mobTyperReportFilePath) throws IOException, PostProcessingException {
        List<Map<String, String>> mobTyperReport = new ArrayList<Map<String, String>>();

        BufferedReader mobTyperReportReader = new BufferedReader(new FileReader(mobTyperReportFilePath.toFile()));

        try {


            String mobTyperReportHeaderLine = mobTyperReportReader.readLine();
            String[] mobTyperReportHeaders = mobTyperReportHeaderLine.split("\t");
            String line;
            while ((line = mobTyperReportReader.readLine()) != null) {
                String[] record = line.split("\t");
                Map mobTyperReportEntry = new HashMap<String, String>();
                for(int i = 0; i < mobTyperReportHeaders.length; i++ ) {
                    mobTyperReportEntry.put(mobTyperReportHeaders[i], record[i]);
                }
                mobTyperReport.add(mobTyperReportEntry);
            }
        } finally {
            // make sure to close, even in cases where an exception is thrown
            mobTyperReportReader.close();
        }

        return mobTyperReport;
    }

    /**
     * The {@link AnalysisType} this {@link AnalysisSampleUpdater} corresponds to.
     *
     * @return The {@link AnalysisType} this {@link AnalysisSampleUpdater}
     *         corresponds to.
     */
    @Override
    public AnalysisType getAnalysisType() {
        return PlasmidScreenPlugin.PLASMID_SCREEN;
    }
}
