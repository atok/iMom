package com.github.atok.imom.model

import com.github.atok.imom.IdGenerator

public data class HtmlContent(val html: String, override val id: String = IdGenerator.generate()) : HasId