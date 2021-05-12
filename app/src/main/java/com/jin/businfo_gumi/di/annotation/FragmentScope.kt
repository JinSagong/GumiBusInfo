package com.jin.businfo_gumi.di.annotation

import javax.inject.Scope

@Target(
    AnnotationTarget.TYPE,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION
)
@Scope
@Retention
annotation class FragmentScope