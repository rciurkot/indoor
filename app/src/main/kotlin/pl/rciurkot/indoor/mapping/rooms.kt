package pl.rciurkot.indoor.mapping

/**
 * Created by rafalciurkot on 03.01.15.
 */
public trait BuildingComponent{
    val name: String
}

//TODO: rozważyć czy taki trait ma sens
public class Outside(override var name: String = "na zewnątrz") : BuildingComponent

public class ClassRoom(override var name: String) : BuildingComponent

public trait Corridor : BuildingComponent
