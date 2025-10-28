# Estructura del projecte APP d’Abasta - Frontend

Aquest document descriu en detall l'organització de carpetes i fitxers del projecte frontend.

## Arquitectura Modular

El projecte està dividit en mòduls clars, que separen la lògica de negoci, la presentació i la comunicació amb serveis externs. 

## Estructura de Directoris

/src
│
├── /assets → Recursos estàtics (imatges, icones, estils)
├── /components → Components React reutilitzables
├── /config → Configuracions globals i constants
├── /context → React Contexts per a estats globals
├── /docs → Documentació tècnica i funcional
├── /hooks → React hooks personalitzats
├── /pages → Components que representen pàgines completes
├── /routes → Definició i protecció de rutes
├── /services → Capa d'accés a dades i APIs
├── /types → Tipus i interfícies TypeScript globals
└── /utils → Funcions utilitàries independents de la UI

