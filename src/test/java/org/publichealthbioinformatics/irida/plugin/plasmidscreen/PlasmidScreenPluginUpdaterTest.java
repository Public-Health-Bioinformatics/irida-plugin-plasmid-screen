package org.publichealthbioinformatics.irida.plugin.plasmidscreen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import org.junit.*;
import org.mockito.ArgumentCaptor;
import org.hamcrest.collection.IsMapContaining;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.description.*;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;


public class PlasmidScreenPluginUpdaterTest {
    private String WORKFLOW_NAME = "plasmid-screen";
    private String WORKFLOW_VERSION = "0.1.0";

    private PlasmidScreenPluginUpdater updater;

    private SampleService sampleService;
    private MetadataTemplateService metadataTemplateService;
    private IridaWorkflowsService iridaWorkflowsService;
    private IridaWorkflow iridaWorkflow;
    private IridaWorkflowDescription iridaWorkflowDescription;

    private UUID uuid = UUID.randomUUID();

    @Before
    public void setUp() throws IridaWorkflowException {
        sampleService = mock(SampleService.class);
        metadataTemplateService = mock(MetadataTemplateService.class);
        iridaWorkflowsService = mock(IridaWorkflowsService.class);
        iridaWorkflow = mock(IridaWorkflow.class);
        iridaWorkflowDescription = mock(IridaWorkflowDescription.class);

        updater = new PlasmidScreenPluginUpdater(metadataTemplateService, sampleService, iridaWorkflowsService);

        when(iridaWorkflowsService.getIridaWorkflow(uuid)).thenReturn(iridaWorkflow);
        when(iridaWorkflow.getWorkflowDescription()).thenReturn(iridaWorkflowDescription);
        when(iridaWorkflowDescription.getName()).thenReturn(WORKFLOW_NAME);
        when(iridaWorkflowDescription.getVersion()).thenReturn(WORKFLOW_VERSION);
    }

    @Test
    public void testUpdate() throws Throwable {

        ImmutableMap<String, String> expectedResults = ImmutableMap.<String, String>builder()
                .put("plasmid-screen/KPC/detected", "False")
                .put("plasmid-screen/KPC/alleles", "")
                .put("plasmid-screen/NDM/detected", "True")
                .put("plasmid-screen/NDM/alleles", "NDM-1")
                .put("plasmid-screen/NDM/replicons", "IncFIB,IncFII,IncFII")
                .put("plasmid-screen/NDM/nearest_genbank_plasmid", "KY798505")
                .put("plasmid-screen/NDM/mash_distance", "0.0167251")
                .put("plasmid-screen/NDM/plasmid_cluster", "1577")
                .put("plasmid-screen/OXA/detected", "True")
                .put("plasmid-screen/OXA/alleles", "OXA-9")
                .put("plasmid-screen/OXA/replicons", "IncFIB,IncFII,IncFII")
                .put("plasmid-screen/OXA/nearest_genbank_plasmid", "KY798505")
                .put("plasmid-screen/OXA/mash_distance", "0.0167251")
                .put("plasmid-screen/OXA/plasmid_cluster", "1577")
                .put("plasmid-screen/VIM/detected", "False")
                .put("plasmid-screen/VIM/alleles", "")
                .put("plasmid-screen/IMP/detected", "False")
                .put("plasmid-screen/IMP/alleles", "")
                .build();

        Path mobTyperReportFilePath = Paths.get(ClassLoader.getSystemResource("SAMN13042171-plasmids_mob_typer_report.tsv").toURI());
        Path abricateReportFilePath = Paths.get(ClassLoader.getSystemResource("SAMN13042171-abricate_report_screened.tsv").toURI());
        Path geneDetectionStatusReportFilePath = Paths.get(ClassLoader.getSystemResource("SAMN13042171-gene_detection_status.tsv").toURI());

        AnalysisOutputFile mobTyperReportFile = new AnalysisOutputFile(mobTyperReportFilePath, null, null, null);
        AnalysisOutputFile abricateReportFile = new AnalysisOutputFile(abricateReportFilePath, null, null, null);
        AnalysisOutputFile geneDetectionStatusReportFile = new AnalysisOutputFile(geneDetectionStatusReportFilePath, null, null, null);

        Map<String, AnalysisOutputFile> analysisOutputFiles = ImmutableMap.of(
                "plasmids_mob_typer_report", mobTyperReportFile,
                "abricate_report_screened", abricateReportFile,
                "gene_detection_status", geneDetectionStatusReportFile
        );

        Analysis analysis = new Analysis(null, analysisOutputFiles, null, null);
        AnalysisSubmission submission = AnalysisSubmission.builder(uuid)
                .inputFiles(ImmutableSet.of(new SingleEndSequenceFile(null))).build();

        submission.setAnalysis(analysis);

        Sample sample = new Sample();
        sample.setId(0L);

        ImmutableMap<MetadataTemplateField, MetadataEntry> metadataMap = ImmutableMap
                .of(new MetadataTemplateField("", ""), new MetadataEntry("", ""));
        when(metadataTemplateService.getMetadataMap(any(Map.class))).thenReturn(metadataMap);

        updater.update(Lists.newArrayList(sample), submission);

        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);

        //this is the important bit.  Ensures the correct values got pulled from the file
        verify(metadataTemplateService).getMetadataMap(mapCaptor.capture());
        Map<String, MetadataEntry> metadata = mapCaptor.getValue();

        int found = 0;
        for (Map.Entry<String, MetadataEntry> e : metadata.entrySet()) {

            if (expectedResults.containsKey(e.getKey())) {
                String expected = expectedResults.get(e.getKey());

                MetadataEntry value = e.getValue();

                assertEquals("metadata values should match", expected, value.getValue());
                found++;
            }
        }
        assertEquals("should have found the same number of results", expectedResults.keySet().size(), found);

        // this bit just ensures the merged data got saved
        verify(sampleService).updateFields(eq(sample.getId()), mapCaptor.capture());
        Map<MetadataTemplateField, MetadataEntry> value = (Map<MetadataTemplateField, MetadataEntry>) mapCaptor
                .getValue().get("metadata");

        assertEquals(metadataMap.keySet().iterator().next(), value.keySet().iterator().next());
    }

    @Test
    public void testParseMobTyperReportFile() throws Throwable {
        Path mobTyperReportFilePath = Paths.get(ClassLoader.getSystemResource("SAMN13042171-plasmids_mob_typer_report.tsv").toURI());
        List<Map<String, String>> mobTyperReport = updater.parseMobTyperReportFile(mobTyperReportFilePath);
        for (Map<String, String> mobTyperReportEntry:mobTyperReport) {
            assertThat(mobTyperReportEntry, IsMapContaining.hasKey("file_id"));
            assertThat(mobTyperReportEntry, IsMapContaining.hasKey("rep_types"));
            assertThat(mobTyperReportEntry, IsMapContaining.hasKey("mash_nearest_neighbor"));
            assertThat(mobTyperReportEntry, IsMapContaining.hasKey("mash_neighbor_distance"));
            assertThat(mobTyperReportEntry, IsMapContaining.hasKey("mash_neighbor_cluster"));
        }
    }

    @Test
    public void testParseAbricateReportFile() throws Throwable {
        Path abricateReportFilePath = Paths.get(ClassLoader.getSystemResource("SAMN13042171-abricate_report_screened.tsv").toURI());
        List<Map<String, String>> abricateReport = updater.parseAbricateReportFile(abricateReportFilePath);
        for (Map<String, String> abricateReportEntry:abricateReport) {
            assertThat(abricateReportEntry, IsMapContaining.hasKey("file"));
            assertThat(abricateReportEntry, IsMapContaining.hasKey("gene"));
            assertThat(abricateReportEntry, IsMapContaining.hasKey("percent_coverage"));
            assertThat(abricateReportEntry, IsMapContaining.hasKey("percent_identity"));
        }
    }

    @Test
    public void testParseGeneDetectionStatusReportFile() throws Throwable {
        Path geneDetectionStatusReportFilePath = Paths.get(ClassLoader.getSystemResource("SAMN13042171-gene_detection_status.tsv").toURI());
        List<Map<String, String>> geneDetectionStatusReport = updater.parseGeneDetectionStatusReportFile(geneDetectionStatusReportFilePath);
        for (Map<String, String> geneDetectionStatusReportEntry:geneDetectionStatusReport) {
            assertThat(geneDetectionStatusReportEntry, IsMapContaining.hasKey("gene_name"));
            assertThat(geneDetectionStatusReportEntry, IsMapContaining.hasKey("detected"));
            assertThat(geneDetectionStatusReportEntry, IsMapContaining.hasKey("alleles"));
        }
    }
}