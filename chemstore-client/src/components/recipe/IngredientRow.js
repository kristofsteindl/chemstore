const IngredientRow = props => {
    const row = props.row
    const ingredient = row.ingredient
    return (
        <li>
            <div className="row" >
                <div className="col-sm-1">
                    <i>{row.amount} {row.unit}</i>
                </div>
                <div className="col-4">
                    <span className="mx-auto">{ingredient.shortName ? ingredient.shortName : ingredient.name}</span>
                </div>
                
                   

                </div>  
        </li>

    )
}

export default IngredientRow