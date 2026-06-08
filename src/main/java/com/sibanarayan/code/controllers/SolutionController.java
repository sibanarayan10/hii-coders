package com.sibanarayan.code.controllers;

import com.sibanarayan.code.config.JwtFilter;
import com.sibanarayan.code.enums.ProgrammingLanguage;
import com.sibanarayan.code.models.request.SolutionRequest;
import com.sibanarayan.code.services.SolutionService;
import com.sibanarayan.code.utility.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/solution")
@AllArgsConstructor
public class SolutionController {
    private final SolutionService solutionService;
    private final JwtFilter filter;
    private  final JwtUtility utility;

    @PostMapping
    public ResponseEntity<Boolean> saveSolution(@RequestBody SolutionRequest solution){
         boolean result=solutionService.saveSolution(solution);
        return new ResponseEntity<>(result,HttpStatus.ACCEPTED);
    }
    @GetMapping
    public ResponseEntity<String> getSolution(@RequestParam ProgrammingLanguage language,@RequestParam UUID problemId, HttpServletRequest request){
        String token=filter.extractTokenFromCookie(request);
        UUID userId=utility.getUserId(token);
        return new ResponseEntity<>(solutionService.getSolution(language,userId,problemId), HttpStatus.ACCEPTED);
    }
}
