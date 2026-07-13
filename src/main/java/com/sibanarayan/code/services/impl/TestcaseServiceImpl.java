package com.sibanarayan.code.services.impl;

import com.sibanarayan.code.entities.TestCase;
import com.sibanarayan.code.models.embeddings.Block;
import com.sibanarayan.code.models.request.TestCaseRequest;
import com.sibanarayan.code.models.response.TestCaseAdminResponse;
import com.sibanarayan.code.models.response.TestCaseExecutionResponse;
import com.sibanarayan.code.models.response.TestCasePreviewResponse;
import com.sibanarayan.code.repository.ProblemRepository;
import com.sibanarayan.code.repository.TestCaseRepository;
import com.sibanarayan.code.services.ImageService;
import com.sibanarayan.code.services.ProblemService;
import com.sibanarayan.code.services.TestcaseService;
import com.sibanarayan.shared_package.enums.RecordStatus;
import com.sibanarayan.shared_package.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class TestcaseServiceImpl implements TestcaseService {
    private final TestCaseRepository testCaseRepository;
    private final ProblemService problemService;
    private final ImageService imageService;


    @Override
    public List<TestCaseAdminResponse> getAllTestCaseByProblemIdForAdmin(UUID problemId) {
        return getAllTestCaseByProblemId(problemId,null).stream().map(this::buildResponseForAdmin).toList();
    }

    @Override
    public List<TestCasePreviewResponse> getAllTestCaseByProblemIdForPreview(UUID problemId) {
        return getAllTestCaseByProblemId(problemId,false).stream().map(this::buildResponseForPreview).toList();
    }

    @Override
    public List<TestCaseExecutionResponse> getAllTestCaseByProblemIdForExecution(UUID problemId) {
        return getAllTestCaseByProblemId(problemId,null).stream().map(this::buildResponseForExecution).toList();
    }

    @Override
    public TestCase getTestcaseById(UUID testcaseId) {
        Optional<TestCase> testcaseWrapper=testCaseRepository.findById(testcaseId);
        if(testcaseWrapper.isEmpty()){
            throw new ResourceNotFoundException("No testcase found with this ID");
        }

        return testcaseWrapper.get();
    }

    @Override
    @Transactional
    public void deleteTestcase(UUID testcaseId) {
        TestCase testCase=getTestcaseById(testcaseId);
        List<Block> blocks=testCase.getDisplayInput()
                .stream()
                .filter(i->i.getType().equals("image"))
                .toList();

        List<String>imgUrls=extractPublicUrl(blocks);
        try{
            for(String imgPublicUrl:imgUrls){
                imageService.deleteImage(imgPublicUrl);
            }
        }catch(IOException e){
           throw new RuntimeException(e);
        }

        testCaseRepository.delete(testCase);
        log.info("Testcase deleted successfully");
    }

    @Override
    public void updateTestcase(TestCaseRequest request) {
        TestCase testCase=getTestcaseById(request.getId());
        problemService.existById(request.getProblemId(),RecordStatus.ACTIVE);

        testCase.setHidden(request.getHidden());
        testCase.setDisplayInput(request.getDisplayInput());
        testCase.setDisplayOutput(request.getDisplayOutput());
        testCase.setInput(request.getInput());
        testCase.setExpectedOutput(request.getExpectedOutput());
        testCase.setExplanation(request.getExplanation());

        testCaseRepository.save(testCase);

        log.info("Testcase-{} get updated for the problem-{} successfully",request.getId(),testCase.getProblemId());
    }

    @Override
    @Transactional
    public void createAllTestcase(List<TestCaseRequest> list,UUID problemId) {
        if(list.isEmpty()){
            throw new IllegalArgumentException("No testcases found in the request");
        }

        problemService.existById(problemId, RecordStatus.ACTIVE);

        for(int i=0;i<list.size();i++){
            createTestCase(list.get(i),i+1,problemId);
        }

        log.info("All testcase for problem-{} got created successfully",list.getFirst().getProblemId());
    }

    private List<TestCase> getAllTestCaseByProblemId(UUID problemId,Boolean hiddenOnly) {

        problemService.existById(problemId,RecordStatus.ACTIVE);
        if(hiddenOnly==null){
            return testCaseRepository.getByProblemIdAndRecordStatus(problemId,RecordStatus.ACTIVE);
        }
        return testCaseRepository.getByProblemIdAndHiddenAndRecordStatus(problemId,hiddenOnly,RecordStatus.ACTIVE);
    }

    private TestCaseAdminResponse buildResponseForAdmin(TestCase testCase){
        return TestCaseAdminResponse.builder()
                .id(testCase.getId())
                .hidden(testCase.isHidden())
                .explanation(testCase.getExplanation())
                .displayInput(testCase.getDisplayInput())
                .displayOutput(testCase.getDisplayOutput())
                .input(testCase.getInput())
                .output(testCase.getExpectedOutput())
                .problemId(testCase.getProblemId())
                .build();
    }
    private TestCaseExecutionResponse buildResponseForExecution(TestCase testCase){
        return TestCaseExecutionResponse.builder()
                .id(testCase.getId())
                .problemId(testCase.getProblemId())
                .input(testCase.getInput())
                .output(testCase.getExpectedOutput())
                .build();
    }

    private TestCasePreviewResponse buildResponseForPreview(TestCase testCase) {
        return TestCasePreviewResponse.builder()
                .id(testCase.getId())
                .problemId(testCase.getProblemId())
                .displayInput(testCase.getDisplayInput())
                .displayOutput(testCase.getDisplayOutput())
                .explanation(testCase.getExplanation())
                .build();

    }
    private void createTestCase(TestCaseRequest request,int sequenceOrder,UUID problemId) {

        TestCase testCase = TestCase.builder()
                .problemId(problemId)
                .hidden(request.getHidden())
                .expectedOutput(request.getExpectedOutput())
                .input(request.getInput())
                .sequenceOrder(sequenceOrder)
                .displayInput(request.getDisplayInput())
                .displayOutput(request.getDisplayOutput())
                .explanation(request.getExplanation())
                .build();

        testCaseRepository.save(testCase);
        log.info("Testcase-{} got created successfully",sequenceOrder);

    }

    private List<String> extractPublicUrl(List<Block> blocks){
        List<String> imageUrls=new ArrayList<>();

        for(Block block:blocks){
            if (!"image".equals(block.getType())) {
                continue;
            }

            Map<String, Object> data = block.getData();
            if (data == null) {
                continue;
            }
            Object fileObj = data.get("file");
            if (fileObj instanceof Map<?, ?> fileMap) {
                Object url = fileMap.get("url");
                if (url instanceof String urlStr && !urlStr.isBlank()) {
                    imageUrls.add(urlStr);
                }
            }
        }

        return imageUrls;
    }

}
