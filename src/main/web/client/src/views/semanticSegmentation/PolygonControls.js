import React from 'react'
import styled, { keyframes } from 'styled-components'
import { getHorizontallyCentralPoint, getVerticallyLowestPoint } from './pointsUtils'

const fadeInScale = keyframes`
  from {
    opacity: 0;
    transform: scale(0);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
`

const Container = styled.div`
  background: white;
  border-radius: 2px;
  box-shadow:
    0px 1px 5px 0px rgba(0, 0, 0, 0.2),
    0px 2px 2px 0px rgba(0, 0, 0, 0.14),
    0px 3px 1px -2px rgba(0, 0, 0, 0.12);
  
  transform-origin: top left;
  animation: ${fadeInScale} 0.31s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  overflow: hidden;
  margin-left: -50%;
  margin-right: 50%
`

const Button = styled.div`
  background: whitesmoke;
  border: 0;
  box-sizing: border-box;
  color: #363636;
  cursor: pointer;
  font-size: 1rem;
  margin: 0;
  outline: 0;
  padding: 8px 16px;
  text-align: center;
  text-shadow: 0 1px 0 rgba(0,0,0,0.1);
  width: 100%;
  transition: background 0.21s ease-in-out;
  &:focus, &:hover {
    background: #eeeeee;
  }
`

function PolygonControls (props) {
    const { geometry } = props.annotation
    // Only show polygon controls if there are at least three points set
    if (!geometry || !geometry.points || geometry.points.length < 2) return null

    return (
        <div
            style={{
                position: 'absolute',
                left: `${getHorizontallyCentralPoint(geometry.points)}%`,
                //top: `${getVerticallyLowestPoint(geometry.points)}%`,
                top: `${(getVerticallyLowestPoint(geometry.points) + 3)}%`,
                ...props.style
            }}
        >
            <Container
                className={props.className}
            >
                <Button onClick={props.onSelectionClear}>Clear</Button>
                <Button onClick={props.onSelectionComplete}>Done</Button>
            </Container>
        </div>
    )
}

PolygonControls.defaultProps = {
    className: '',
    style: {}
}

export default PolygonControls