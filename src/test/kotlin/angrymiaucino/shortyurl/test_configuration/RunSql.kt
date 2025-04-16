package angrymiaucino.shortyurl.test_configuration

@Target(AnnotationTarget.FUNCTION)
annotation class RunSql (val scripts : Array<String>)
