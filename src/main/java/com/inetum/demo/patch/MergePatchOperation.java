package com.inetum.demo.patch;

import com.inetum.demo.dtos.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Documentación OpenAPI común de los endpoints PATCH con JSON Merge Patch. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "partially update an object",
        description = "JSON Merge Patch (RFC 7386): only the fields sent are modified. "
                + "Content-Type must be application/merge-patch+json"
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Object updated"),
        @ApiResponse(responseCode = "400",
                description = "Malformed JSON or invalid resulting value",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Object not found",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "415", description = "Content-Type is not application/merge-patch+json",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponseDto.class)))
})
public @interface MergePatchOperation {
}
