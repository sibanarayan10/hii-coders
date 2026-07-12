package com.sibanarayan.code.models.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class CreateTestcaseRequest {
   private List<TestCaseRequest> testCases;
   private UUID problemId;
}
