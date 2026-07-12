package com.sibanarayan.code.models.response;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.code.enums.SolveStatus;
import com.sibanarayan.code.models.embeddings.Block;
import com.sibanarayan.code.models.request.CreateProblemRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public record ProblemUserEngagementResponse (String title,
                                     List<Block> blocks,
                                     boolean saved,
                                     boolean liked,
                                     boolean favorite,
                                     Integer totalLikes,
                                     Integer totalDislikes,
                                     Double acceptanceRate,
                                     SolveStatus status,
                                     UUID id,
                                     ProblemDifficulty difficulty,
                                     Set<ProblemsCategory> categories
                                     ){}