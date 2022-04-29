import { Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from "@mui/material"
import { useState } from "react"
import ItemInputRow from "./ItemInputRow"

const ItemInputsInMixture = props => {
    const { recipe, amount, ingredients, setSelectedItems, selectedItems, type } = props 
    const [ spacer, setSpacer ] = useState(0)

    const isChemItems = type === "CHEM_ITEM"

    return (
        
        <div>
            {ingredients && ingredients.length > 0 && ( 
                <div style={{paddingBottom: "20px"}}>
                    <h3 className="display-8">{isChemItems ? "Chemical" : "Mixture"} ingredients</h3>
                    <TableContainer component={Paper}>
                        <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
                            <TableHead>
                                <TableRow>
                                    <TableCell align="left"><strong>ingredient name</strong></TableCell>
                                    <TableCell align="left"><strong>{isChemItems ? "manufacturer, batch-seq" : "mixture id"}</strong></TableCell>
                                    <TableCell align="left"><strong>amount</strong></TableCell>
                                    <TableCell align="left"><strong>unit</strong></TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {ingredients.map((chemicalIngredient, index) => 
                                    <ItemInputRow 
                                        key={chemicalIngredient.ingredient.id}
                                        type={type}
                                        amountFactor={amount / recipe.amount}
                                        ingredientRow={chemicalIngredient} 
                                        setSelectedItems={setSelectedItems} 
                                        selectedItems={selectedItems}
                                        posFromLast={ingredients.length - index - 1}
                                        setSpacer={setSpacer}
                                    />

                                )}
                                <TableCell style={{height: `${spacer}px`}}/>
                                
                            </TableBody>
                        </Table>
                    </TableContainer>
                </div>)
            }
        </div>
    )
}

export default ItemInputsInMixture