package com.mable.bank.controller

import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

abstract class AbstractController {
    fun buildLocationHeader(id: Long): URI {
        return ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(id)
            .toUri()
    }
}
