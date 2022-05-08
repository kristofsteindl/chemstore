import { Table, Paper, TableContainer, TableHead, TableRow, TableCell, TableBody } from "@mui/material"

const IngredientMixtureTable = props => {
    const { mixture, type } = props
    const recipe = mixture.recipe
    const ingredients = type === "CHEM_ITEM" ? recipe.chemicalIngredients : recipe.recipeIngredients
    
    const getMixtureRow = recipeIngredient => {
        const recipeId = recipeIngredient.ingredient.id
        const mixtureItem = mixture.mixtureItems.filter(mi => mi.recipe.id === recipeId)[0]
        return ({
            id: mixtureItem.id,
            name: mixtureItem.recipe.name,
            amount: recipeIngredient.amount / recipe.amount * mixture.amount,
            unit: recipeIngredient.unit,
            manufacturerName: "",
            identifier: mixtureItem.id
        })
    }

    const getChemItemRow = chemicalIngredient => {
        const chemicalId = chemicalIngredient.ingredient.id
        const chemItem = mixture.chemItems.filter(chemItem => chemItem.chemical.id === chemicalId)[0]
        const chemical = chemItem.chemical
        return ({
            id: chemicalId,
            name: chemical.shortName,
            amount: chemicalIngredient.amount / recipe.amount * mixture.amount,
            unit: chemicalIngredient.unit,
            identifier: `${chemItem.batchNumber}-${chemItem.seqNumber}`,
            manufacturerName: chemItem.manufacturer.name,
        })
    }

    const getRow = ingredient => {
        return type === "CHEM_ITEM" ? getChemItemRow(ingredient) : getMixtureRow(ingredient)
    }

    return (
        <TableContainer component={Paper}>
            <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
                <TableHead>
                    <TableRow>
                        <TableCell align="left"><strong>ingredient name</strong></TableCell>
                        <TableCell align="left"><strong>amount</strong></TableCell>
                        <TableCell align="left"><strong>{type === "CHEM_ITEM" ? "batch-seq" : "mixture id"}</strong></TableCell>
                        <TableCell align="left" style={{opacity: type === "CHEM_ITEM" ? "1.0": "0.0"}}>
                            <strong>manufacturer</strong>
                        </TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                {ingredients.map((ing) => {
                    const ingredientRow = getRow(ing)
                    return (<TableRow key={ingredientRow.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                        <TableCell>{ingredientRow.name}</TableCell>
                        <TableCell>{ingredientRow.amount} {ingredientRow.unit}</TableCell>
                        <TableCell>{ingredientRow.identifier}</TableCell>
                        <TableCell align="left">{ingredientRow.manufacturerName}</TableCell>
                    </TableRow>)
                })}
                </TableBody>
            </Table>
        </TableContainer>
    )
}

export default IngredientMixtureTable