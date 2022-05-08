import axios from "axios"
import { useEffect, useState } from "react"
import Select from "react-dropdown-select"
import { useSelector } from "react-redux"
import { check } from "../../utils/securityUtils"
import Pagination from "../Pagination"
import RedirectFormButton from "../RedirectFormButton"
import VerifyPanel from "../UI/VerifyPanel"
import MixtureCard from "./MixtureCard"
import MixtureHeader from "./MixtureHeader"

const MixtureDashboard = () => {
    const PAGE_LIMIT = 10

    const selectedLab = useSelector(state => state.selectedLab)
    const user = useSelector((state) => state.security.user)

    const [ mixtures, setMixtures ] = useState([])
    const [ projects, setProjects ] = useState([])
    
    const [ selectedProject, setSelectedProject ] = useState("")
    const [onlyAvailable, setOnlyAvailable] = useState(true)
    const [error, setError] = useState(true)
    
    const [totalItems, setTotalItems] = useState(1)
    const [ page, setPage ] = useState(1)
    const [ size, setSize ] = useState(PAGE_LIMIT)
    
    const queryString = () => {
        let queryString = `?page=${page - 1}&size=${size}`
        queryString = `${queryString}${selectedProject ? ('&projectId=' + selectedProject.id) : ''}`
        queryString = `${queryString}${onlyAvailable ? '&available=true': ''}`
        return queryString
    }

    const isManager =  (selectedLab.key) && selectedLab.labManagers.filter(manager => manager.username === user.username).length > 0

    useEffect(() => {
        if (selectedLab) {
            setSelectedProject("")
            axios.get(`/api/logged-in/project/${selectedLab.key}`).then(result => setProjects(result.data))
        } 
    }, [selectedLab])

    useEffect(() => {
        check()
        if (selectedLab) {
            axios.get(`/api/mixture/${selectedLab.key}${queryString()}`)
                .then(result => {
                    setMixtures(result.data.content)
                    setTotalItems(result.data.totalItems)
                    
                })
        }
    }, [selectedLab, page, size, selectedProject, onlyAvailable])
   

    const deleteMixture = async mixtureId => {
        try {
            await axios.delete(`/api/mixture/${mixtureId}`)
            setMixtures(originalList => originalList.filter(mixture => mixture.id !== mixtureId))
        } catch (error) {
            setError(error.response.data.message)
        }
        
    }


    const getMixtureTable = () => {
        if (mixtures.length === 0) {
            return <p className="lead"><i>There is no mixture in the lab (according to the filter criteria)</i></p>
        } else {
            return (<div>
                <MixtureHeader />
                <hr />
                {mixtures.map(mixture => 
                    <MixtureCard 
                        key={mixture.id} 
                        mixture={mixture}
                        deleteMixture={deleteMixture}
                        isManager={isManager}
                    />
                )}
            </div>)
        }
    }

    return (
        <div className="container col-md-12">
            <div className="row" style={{ position: "relative"}}>
                {isManager && 
                    <div style={{ position: "absolute", bottom: "0",left: "0"}}>
                        <RedirectFormButton formRoute="/add-mixture" buttonLabel="Add Mixture"/>
                    </div>
                }
                <div className="text-center">
                    <h3 className="display-4">Mixtures</h3>
                    <p className="lead">List the mixtures and eluents of {selectedLab.name}</p>
                </div>
            </div>
            <br/>
            <div className="w-300 px-4 py-0 d-flex flex-row flex-wrap align-items-center justify-content-between">
                <div className="col-sm-4 d-flex py-2 align-items-center">
                    <div className="col-sm-4">
                        <Pagination 
                            totalRecords={totalItems}
                            pageLimit={PAGE_LIMIT} 
                            pageNeighbours={1}
                            onPageChanged={paginatioData => setPage(paginatioData.currentPage)}
                        />
                    </div>

                    <div className="col-sm-8 pad-chckbx" >
                        <input
                            type="checkbox"
                            checked={onlyAvailable}
                            onChange={() => setOnlyAvailable(!onlyAvailable)}
                        />
                        <label className="pad-5" >Only available</label>
                        
                    </div>

                </div>
                <div className="col-sm-8">
                    <Select
                        options={projects}
                        labelField="name"
                        valueField="name"
                        values={projects.filter(project => selectedProject && (project.id === selectedProject.id))}
                        placeholder="project"
                        searchable={false}
                        clearable={false}
                        style={{height: "42px", fontSize: "16px"}}
                        onChange={items => setSelectedProject(items[0] ? items[0] : "")}
                    />
                </div>
            </div>
            <hr />
            {getMixtureTable()} 
            {error && 
                <VerifyPanel 
                    onCancel={() => setError("")} 
                    veryfyMessage={error}
                    buttonLabel="Ok"
                />}
        </div>
    )
}

export default MixtureDashboard