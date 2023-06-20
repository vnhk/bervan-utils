# **History Framework**

____

#### Framework allows to :

- create history as separated object
- create history in the dedicated table in the database
- perform diff between object and history object

#### Auto configuration (check example in app/com.bervan.demo.autoconfiguration) setup for entity

1. Your entity have to implements AbstractBaseEntity<ID_CLASS> and be annotated with @HistorySupport, example:
   ProjectTwo class
2. Create entity that will be the history class for your entity that contain entity fields that you want to have history
   of, that implements AbstractBaseHistoryEntity<ID_CLASS>, example: ProjectHistoryTwo class
3. Create @OneToMany relation in entity class as collection of history objects. The field should be annotated with
   @HistoryCollection(historyClass = YOUR_ENTITY_CLASS.class)
4. Create @ManyToOne relation in history class as reference to entity. The field should be annotated with
   @HistoryOwnerEntity
5. Repositories for entity and history classes should extend only **BaseRepository<CLASS, ID_CLASS>**
6. @EnableJpaRepositories(basePackages = "your packages with repositories", repositoryBaseClass =
   BaseRepositoryImpl.class) in your Spring config

#### Manual configuration setup