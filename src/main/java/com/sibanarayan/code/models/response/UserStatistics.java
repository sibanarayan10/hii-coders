package com.sibanarayan.code.models.response;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.shared_package.enums.SubmissionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
public class UserStatistics {
    private Map<ProblemDifficulty,Integer> submissionByDifficulty;
    private  Map<SubmissionStatus,Integer> submissionByStatus;
    private Map<ProblemsCategory,Integer> submissionByCategory;
    private Integer totalSubmission;
}
