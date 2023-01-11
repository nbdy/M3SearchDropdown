package io.eberlein.m3searchdropdown

interface ISearchableViewModel <EntityType: ISearchableEntity> {
    fun search(name: String, cb: (List<EntityType>) -> Unit)
}
