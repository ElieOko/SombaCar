package t3digitalgroup.vehnixauto.server.route.glossary

import t3digitalgroup.vehnixauto.server.route.GlobalRoute

object GlossaryScope {
    const val PUBLIC = "${GlobalRoute.PUBLIC}/${GlossaryFeatures.GLOSSARY_PATH}"
    const val PROTECTED = "${GlobalRoute.PROTECT}/${GlossaryFeatures.GLOSSARY_PATH}"
}

object GlossaryFeatures {
    const val GLOSSARY_PATH = "glossary"
}
