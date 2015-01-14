package pl.rciurkot.indoor.mapping

/**
 * Created by rafalciurkot on 03.01.15.
 */
public trait BuildingComponent

//TODO: rozważyć czy taki trait ma sens
public trait Outside : BuildingComponent

public trait ClassRoom : BuildingComponent

public trait Corridor : BuildingComponent
