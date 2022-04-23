const MixtureRow = props => {
    const ingredient = props.ingredient
    return (
        <li>
            <div className="row" >
                <div className="col-sm-1">
                    <i>{ingredient.amount} {ingredient.unit}</i>
                </div>
                <div className="col-2">
                    <span className="mx-auto">{ingredient.name}</span>
                </div>
                <div className="col-2">
                    <span className="mx-auto">{ingredient.manufacturerName}</span>
                </div>
                <div className="col-2">
                    <span className="mx-auto">{ingredient.batchNumber}-{ingredient.seqNumber}</span>
                </div>
                <div className="col-2">
                    <span className="mx-auto">{ingredient.expirationDate}</span>
                </div>
            </div>  
        </li>

    )
}

export default MixtureRow